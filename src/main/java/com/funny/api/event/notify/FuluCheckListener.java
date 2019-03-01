package com.funny.api.event.notify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.config.FuluConfig;
import com.funny.utils.SignUtils;
import com.youzan.open.sdk.util.hash.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
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
        Map map = getFuluHeader("kamenwang.goods.get");
        // 合作商家订单号（唯一不重复）
        map.put("customerorderno", orderFromYouzanEntity.getOrderNo());
        map.put("customerorderno", fuluConfig.getUserId());
        // 将签名添加到URL参数后
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(getRequestString(map), null, String.class);
        Map<String, String> result = objectMapper.readValue(responseEntity.getBody(), Map.class);
        if (result.get("Status") != null && "成功".equals(result.get("Status"))) {
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.SUCCESS);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }

        // 响应错误码为1206时是查询错误：系统出现异常，可持续下单处理，可查询时间超过十分钟以后，不能查到订单可做关单处理或重新下单处理)
        if (result.get("MessageCode") != null && "1206".equals(result.get("MessageCode"))) {
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.WAIT_PROCESS);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }

    }

}