package com.funny.api.event.notify.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.api.event.notify.AbstractFuluListener;
import com.funny.api.event.notify.FuluCheckEvent;
import com.funny.config.FuluConfig;
import com.funny.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 去福禄平台下单监听器
 *
 * @author liyanjun
 */
@Component
public class FuluCheckV2Listener {

    private static final Logger logger = LoggerFactory.getLogger(FuluCheckV2Listener.class);

    @Autowired
    protected FuluConfig fuluConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private WareFuluInfoService wareFuluInfoService;

    @Autowired
    ApplicationContext applicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @EventListener
    public void onApplicationEvent(FuluCheckEvent fuluCheckEvent) throws IOException {
        Integer id = Integer.parseInt(String.valueOf(fuluCheckEvent.getSource()));
        OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
        // 不是充值中状态，不处理
        if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.PROCESS) {
            return;
        }
        Map map = getCommonParam();
        // 合作商家订单号（唯一不重复）
        map.put("CustomerOrderNo", orderFromYouzanEntity.getOrderNo());
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型，这里用json所以是MediaType.APPLICATION_JSON
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
        // 将签名添加到URL参数后
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(fuluConfig.getFuluCheckUrl(), request, String.class);
        Map<String, Object> result = objectMapper.readValue(responseEntity.getBody(), Map.class);
        logger.debug(responseEntity.getBody());
        if (result.get("Status") != null && "成功".equals(result.get("Status"))) {
            WareFuluInfoEntity wareFuluInfoEntity = wareFuluInfoService.queryByOuterSkuId(orderFromYouzanEntity.getWareNo());
            if (wareFuluInfoEntity.getType() == WareFuluInfoEntity.TYPE_IS_CARD) {
                // 卡密类型的，成功后需要提取卡密内容
                orderFromYouzanEntity.setCards(result.get("Cards").toString());
            }
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.SUCCESS);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }

        if (result.get("Status") != null && "失败".equals(result.get("Status"))) {
            orderFromYouzanEntity.setException("充值失败。" + responseEntity.getBody());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }

        if (result.get("MessageCode") != null && "2200".equals(result.get("MessageCode").toString()) && (orderFromYouzanEntity.getLastRechargeTime().getTime() + (660 * 1000)) < System.currentTimeMillis()) {
            logger.debug("充值中的订单【" + orderFromYouzanEntity.getId() + "】，查询超时，退款。");
            orderFromYouzanEntity.setException("查询超时，并且返回2200（外部系统订单号不存在），进行退款。");
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }

        // 响应错误码为1206时是查询错误：系统出现异常，可持续下单处理，可查询时间超过十分钟以后，不能查到订单可做关单处理或重新下单处理)
        // 下单超过10分钟以后，不做这个类型的重发，等查询关单。
        if (result.get("MessageCode") != null && "1206".equals(result.get("MessageCode").toString()) && (orderFromYouzanEntity.getCreateTime().getTime() + (600 * 1000)) > System.currentTimeMillis()) {
            // 设置状态，等定时器重发
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.WAIT_PROCESS);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }

    }

    private Map<String, Object> getCommonParam() {
        Map map = new HashMap(8);
        map.put("AppKey", fuluConfig.getKey());
        map.put("TimeStamp", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        map.put("Format", "json");
        map.put("SignType", "md5");
        map.put("V", "1.0");
        return map;
    }

}