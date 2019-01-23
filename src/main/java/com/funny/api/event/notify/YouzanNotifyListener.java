package com.funny.api.event.notify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.api.praise.entity.AccessToken;
import com.funny.api.praise.entity.YouzanNotifyEventSource;
import com.funny.utils.ConfigUtils;
import com.funny.utils.EncryptUtil;
import com.funny.utils.SignUtils;
import com.youzan.open.sdk.client.auth.Token;
import com.youzan.open.sdk.client.core.DefaultYZClient;
import com.youzan.open.sdk.client.core.YZClient;
import com.youzan.open.sdk.exception.KDTException;
import com.youzan.open.sdk.gen.v3_0_0.api.YouzanLogisticsOnlineConfirm;
import com.youzan.open.sdk.gen.v3_0_0.api.YouzanTradeMemoUpdate;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmParams;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanTradeMemoUpdateParams;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanTradeMemoUpdateResult;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用有赞接口
 */
@Component
@ConditionalOnProperty("optional.youzan.enable")
public class YouzanNotifyListener {
    Logger logger = LoggerFactory.getLogger(YouzanNotifyListener.class);

    private YZClient client;

    private AccessToken accessToken;

    @Value("${optional.youzan.client-id}")
    private String clientId;

    @Value("${optional.youzan.client-secret}")
    private String clientSecret;

    @Value("${optional.youzan.kdt-id}")
    private String kdtId;

    @Autowired
    RestTemplate template;

    ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @EventListener
    public void onApplicationEvent(YouzanNotifyEvent youzanNotifyEvent) {
        try {
            client = getClient();
            if (client == null) {
                // TODO 处理修改卡密已设置，但是却又改变订单状态失败
            }

            YouzanNotifyEventSource youzanNotifyEventSource = (YouzanNotifyEventSource) youzanNotifyEvent.getSource();
            // 修改备注添加卡密信息
            YouzanTradeMemoUpdateParams youzanTradeMemoUpdateParams = new YouzanTradeMemoUpdateParams();
            youzanTradeMemoUpdateParams.setTid(youzanNotifyEventSource.getTid());
            youzanTradeMemoUpdateParams.setMemo(youzanNotifyEventSource.getCardInfoString());

            YouzanTradeMemoUpdate youzanTradeMemoUpdate = new YouzanTradeMemoUpdate();
            youzanTradeMemoUpdate.setAPIParams(youzanTradeMemoUpdateParams);
            YouzanTradeMemoUpdateResult youzanTradeMemoUpdateResult = client.invoke(youzanTradeMemoUpdate);

            if (!youzanTradeMemoUpdateResult.getIsSuccess()) {
                // TODO 处理修改卡密已设置，但是却又改变订单状态失败
            }
            // 改备注成功以后发货
            YouzanLogisticsOnlineConfirmParams youzanLogisticsOnlineConfirmParams = new YouzanLogisticsOnlineConfirmParams();
            youzanLogisticsOnlineConfirmParams.setTid(youzanNotifyEventSource.getTid());
            youzanLogisticsOnlineConfirmParams.setIsNoExpress(1L);

            YouzanLogisticsOnlineConfirm youzanLogisticsOnlineConfirm = new YouzanLogisticsOnlineConfirm();
            youzanLogisticsOnlineConfirm.setAPIParams(youzanLogisticsOnlineConfirmParams);
            client.invoke(youzanLogisticsOnlineConfirm);
            if (!youzanTradeMemoUpdateResult.getIsSuccess()) {
                // TODO 处理修改卡密已设置，但是却又改变订单状态失败
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            // TODO 处理修改卡密已设置，但是却又改变订单状态失败
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

        ResponseEntity<String> response = template.postForEntity("https://open.youzan.com/oauth/token", paramMap, String.class);
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
