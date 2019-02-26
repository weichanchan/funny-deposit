package com.funny.task;

import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.api.event.notify.FuluSubmitEvent;
import com.funny.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Date;
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
            if (orderFromYouzanEntity.getCreateTime().getTime() < (System.currentTimeMillis() + (60 * 1000))) {
                // 没入库够1分钟不理他
                continue;
            }
            map.clear();
            map.put("orderNo", orderFromYouzanEntity.getId());
            List<OrderRequestRecordEntity> orderRequestRecordEntities = orderRequestRecordService.queryList(map);
            if (orderRequestRecordEntities.size() >= 3) {
                // 重试3次了，不发了。退钱
                // TODO 触发退款事件
                continue;
            }
            // 触发重发事件
            applicationContext.publishEvent(new FuluSubmitEvent(orderFromYouzanEntity.getId()));
        }
    }
}
