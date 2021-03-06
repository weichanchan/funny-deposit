package com.funny.task;

import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.api.event.notify.FuluSubmitEvent;
import com.funny.api.event.notify.YouzanRefundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 福禄下单重试任务
 * <p>
 * fuluCheckTask为 bean的名称
 *
 * @author liyanjun
 */
@Component("fuluCheckResendTask")
public class FuluCheckResendTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private OrderRequestRecordService orderRequestRecordService;

    @Autowired
    ApplicationContext applicationContext;

    public void watch() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("status", OrderFromYouzanEntity.WAIT_PROCESS);
        List<OrderFromYouzanEntity> orderFromYouzanEntities = orderFromYouzanService.queryList(map);
        for (OrderFromYouzanEntity orderFromYouzanEntity : orderFromYouzanEntities) {
            if ((orderFromYouzanEntity.getCreateTime().getTime()  + (60 * 1000)) > System.currentTimeMillis()) {
                logger.debug("待充值的订单【" + orderFromYouzanEntity.getId()+"】，入库未够一分钟。还未满足重发条件。");
                continue;
            }
            map.clear();
            map.put("orderNo", orderFromYouzanEntity.getId());
            List<OrderRequestRecordEntity> orderRequestRecordEntities = orderRequestRecordService.queryList(map);
            if (orderRequestRecordEntities.size() >= 3) {
                // 最多重试3次,超过的就跳过，等着超时退款，或者他们通知过来。
                logger.debug("待充值的订单【" + orderFromYouzanEntity.getId()+"】，重发已超过3次。");
                continue;
            }
            logger.debug("待充值的订单【" + orderFromYouzanEntity.getId()+"】，重发。");
            // 触发发送事件
            applicationContext.publishEvent(new FuluSubmitEvent(orderFromYouzanEntity.getId()));
        }
    }
}
