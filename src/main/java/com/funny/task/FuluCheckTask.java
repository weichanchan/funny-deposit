package com.funny.task;

import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.api.event.notify.FuluSubmitEvent;
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
    private OrderRequestRecordService orderRequestRecordService;

    @Autowired
    ApplicationContext applicationContext;

    public void watch() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("status", OrderFromYouzanEntity.PROCESS);
        List<OrderFromYouzanEntity> orderFromYouzanEntities = orderFromYouzanService.queryList(map);
        for (OrderFromYouzanEntity orderFromYouzanEntity : orderFromYouzanEntities) {
            if (orderFromYouzanEntity.getCreateTime().getTime() < (System.currentTimeMillis() + (60 * 1000))) {
                // 没入库够1分钟不理他
                continue;
            }
            if (orderFromYouzanEntity.getCreateTime().getTime() < (System.currentTimeMillis() + (660 * 1000))) {
                // 超过10分钟了，没救了退款
                // TODO 触发退款事件
                continue;
            }
            // TODO 触发查询事件
        }
    }
}
