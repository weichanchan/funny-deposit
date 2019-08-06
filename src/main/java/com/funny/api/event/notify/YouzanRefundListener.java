package com.funny.api.event.notify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.api.praise.entity.AccessToken;
import com.youzan.open.sdk.client.auth.Token;
import com.youzan.open.sdk.client.core.DefaultYZClient;
import com.youzan.open.sdk.client.core.YZClient;
import com.youzan.open.sdk.gen.v3_0_0.api.YouzanTradeRefundSellerActive;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanTradeRefundSellerActiveParams;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanTradeRefundSellerActiveResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * 调用有赞接口
 */
@Component
@ConditionalOnProperty("optional.youzan.enable")
public class YouzanRefundListener {

    private static final Logger logger = LoggerFactory.getLogger(YouzanRefundListener.class);

    private YZClient client;

    private AccessToken accessToken;

    @Value("${optional.youzan.client-id}")
    private String clientId;

    @Value("${optional.youzan.client-secret}")
    private String clientSecret;

    @Value("${optional.youzan.kdt-id}")
    private String kdtId;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;
    @Autowired
    private WareFuluInfoService wareFuluInfoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @EventListener
    public void onApplicationEvent(YouzanRefundEvent youzanNotifyEvent) {
        OrderFromYouzanEntity orderFromYouzanEntity = null;
        try {
            Integer id = Integer.parseInt(String.valueOf(youzanNotifyEvent.getSource()));
            orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
            // 不是失败状态，不处理
            if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.FAIL) {
                return;
            }
            if(StringUtils.isBlank(orderFromYouzanEntity.getSubOrderId())){
                // 请手工退款
                orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.HAND_REFUND);
                orderFromYouzanService.update(orderFromYouzanEntity);
                return;
            }
            WareFuluInfoEntity wareFuluInfoEntity = wareFuluInfoService.queryByOuterSkuId(orderFromYouzanEntity.getWareNo());
            // 非Q币的超人渠道，如果要购买多个，需要拆单，只有一个就不是拆单，可以直接退款
            Integer num = wareFuluInfoEntity.getNum() * orderFromYouzanEntity.getNum();
            if(wareFuluInfoEntity != null && !wareFuluInfoEntity.getWareName().contains("Q币")
                    && !wareFuluInfoEntity.getWareName().contains("q币")
                    && !wareFuluInfoEntity.getWareName().contains("鱼翅")
                    && !wareFuluInfoEntity.getWareName().contains("陌陌币")
                    && !wareFuluInfoEntity.getWareName().contains("喜点")
                    && wareFuluInfoEntity.getRechargeChannel() == WareFuluInfoEntity.TYPE_SUPERMAN_CHANNEL
                    && num > 1){
                // 超人拆单，没法退款
                orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.HAND_REFUND);
                orderFromYouzanService.update(orderFromYouzanEntity);
                return;
            }
            logger.debug("订单：" + orderFromYouzanEntity.getId() + "开始发起退款。退款原因：" + youzanNotifyEvent.getReason());

            YouzanTradeRefundSellerActiveParams youzanTradeRefundSellerActiveParams = new YouzanTradeRefundSellerActiveParams();
            youzanTradeRefundSellerActiveParams.setTid(orderFromYouzanEntity.getYouzanOrderId());
            youzanTradeRefundSellerActiveParams.setRefundFee(orderFromYouzanEntity.getOrderPrice().floatValue());
            youzanTradeRefundSellerActiveParams.setOid(Long.parseLong(orderFromYouzanEntity.getSubOrderId()));
            youzanTradeRefundSellerActiveParams.setDesc(youzanNotifyEvent.getReason() + "");

            YouzanTradeRefundSellerActive youzanTradeRefundSellerActive = new YouzanTradeRefundSellerActive();
            youzanTradeRefundSellerActive.setAPIParams(youzanTradeRefundSellerActiveParams);
            YouzanTradeRefundSellerActiveResult result = getClient().invoke(youzanTradeRefundSellerActive);
            if (!result.getIsSuccess()) {
                // 退款时报
                orderFromYouzanEntity.setException(orderFromYouzanEntity.getException() + objectMapper.writeValueAsString(result.toString()));
                orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.REFUND_FAIL);
                orderFromYouzanService.update(orderFromYouzanEntity);
                logger.debug("订单：" + orderFromYouzanEntity.getId() + "退款失败，原因：" + result.toString());
                return;
            }
            // 退款成功
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.REFUND_SUCCESS);
            orderFromYouzanService.update(orderFromYouzanEntity);
        } catch (Exception e) {
            // 退款失败
            orderFromYouzanEntity.setException(e.getMessage());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.REFUND_FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            // 异常只打印日志，定时任务会继续重试
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 获取有赞 token 用于通讯
     *
     * @return
     *
     * @throws IOException
     */
    private AccessToken getToken() throws IOException {
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id", clientId);
        paramMap.add("client_secret", clientSecret);
        paramMap.add("grant_type", "silent");
        paramMap.add("kdt_id", kdtId);

        ResponseEntity<String> response = restTemplate.postForEntity("https://open.youzan.com/oauth/token", paramMap, String.class);
        Map result = objectMapper.readValue(response.getBody(), Map.class);
        String accessToken = (String) result.get("access_token");
        Integer expiresIn = (Integer) result.get("expires_in");
        if (accessToken == null || expiresIn == null) {
            logger.error(response.getBody());
            return null;
        }
        // 提前过期时间1天刷新Token
        return new AccessToken(accessToken, System.currentTimeMillis() + ((expiresIn - 3600 * 24) * 1000));
    }

    /**
     * 使用有赞 client 前的一些检查
     *
     * @return
     *
     * @throws IOException
     */
    private YZClient getClient() throws IOException {

        if (accessToken == null || accessToken.isExpire()) {
            accessToken = getToken();
            client = new DefaultYZClient(new Token(accessToken.getAccessToken()));
        }

        if (accessToken == null) {
            return null;
        }

        if (client == null) {
            client = new DefaultYZClient(new Token(accessToken.getAccessToken()));
        }

        return client;
    }

}
