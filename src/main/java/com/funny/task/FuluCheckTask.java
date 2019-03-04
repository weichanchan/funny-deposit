package com.funny.task;

import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.api.event.notify.FuluCheckEvent;
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
 * 福禄下单状态主动查询任务
 * <p>
 * fuluCheckTask为 bean的名称
 *
 * @author liyanjun
 */
@Component("fuluCheckTask")
public class FuluCheckTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private ApplicationContext applicationContext;

    public void watch() {
        Map<String, Object> map = new HashMap<>(4);

        // 查找需要退款的
        map.put("status", OrderFromYouzanEntity.FAIL);
        List<OrderFromYouzanEntity> orderFromYouzanEntities = orderFromYouzanService.queryList(map);
        for (OrderFromYouzanEntity orderFromYouzanEntity : orderFromYouzanEntities) {
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), orderFromYouzanEntity.getException()));
        }

        map.put("status", OrderFromYouzanEntity.PROCESS);
        orderFromYouzanEntities = orderFromYouzanService.queryList(map);
        if(orderFromYouzanEntities.size() == 0){
            logger.debug("没用状态为充值中的订单，不需要去查询");
        }

        for (OrderFromYouzanEntity orderFromYouzanEntity : orderFromYouzanEntities) {
            if ((orderFromYouzanEntity.getCreateTime().getTime() + (60 * 1000)) > System.currentTimeMillis()) {
                logger.debug("充值中的订单【" + orderFromYouzanEntity.getId()+"】，入库未够一分钟。");
                // 没入库够1分钟不理他
                continue;
            }
            logger.debug("充值中的订单【" + orderFromYouzanEntity.getId()+"】，查询充值状态");
            applicationContext.publishEvent(new FuluCheckEvent(orderFromYouzanEntity.getId()));
        }
    }
}
