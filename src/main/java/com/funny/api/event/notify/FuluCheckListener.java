package com.funny.api.event.notify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * 去福禄平台下单监听器
 *
 * @author liyanjun
 */
@Component
public class FuluCheckListener extends AbstractFuluListener {

    private static final Logger logger = LoggerFactory.getLogger(FuluCheckListener.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    ApplicationContext applicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @EventListener
    public void onApplicationEvent(FuluCheckEvent fuluCheckEvent) throws IOException {
        Integer id = Integer.parseInt(String.valueOf(fuluCheckEvent.getSource()));
        OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
        // 不是待充值状态，不处理
        if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.PROCESS) {
            return;
        }
        Map map = getFuluHeader("kamenwang.order.get");
        // 合作商家订单号（唯一不重复）
        map.put("customerorderno", orderFromYouzanEntity.getOrderNo());
        map.put("customerid", fuluConfig.getUserId());
        // 将签名添加到URL参数后
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(getRequestString(map), null, String.class);
        Map<String, String> result = objectMapper.readValue(responseEntity.getBody(), Map.class);
        logger.debug(responseEntity.getBody());
        if (result.get("Status") != null && "成功".equals(result.get("Status"))) {
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.SUCCESS);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }

        if (result.get("Status") != null && "失败".equals(result.get("Status"))) {
            orderFromYouzanEntity.setException("充值失败。" + responseEntity.getBody());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "充值失败"));
            return;
        }

        // 响应错误码为1206时是查询错误：系统出现异常，可持续下单处理，可查询时间超过十分钟以后，不能查到订单可做关单处理或重新下单处理)
        if (result.get("MessageCode") != null && "1206".equals(result.get("MessageCode"))) {
            // 设置状态，等定时器重发
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.WAIT_PROCESS);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }

    }

}
