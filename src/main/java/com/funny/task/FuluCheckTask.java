package com.funny.task;

import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.api.event.notify.FuluCheckEvent;
import com.funny.api.event.notify.FuluSubmitEvent;
import com.funny.api.event.notify.YouzanRefundEvent;
import com.funny.api.event.notify.v2.FuluCheckV2Event;
import com.funny.api.event.notify.v2.FuluSubmitV2Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private WareFuluInfoService wareFuluInfoService;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${optional.fulu.v2Enable}")
    private boolean v2Enable;

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
        if (orderFromYouzanEntities.size() == 0) {
            logger.debug("没用状态为充值中的订单，不需要去查询");
        }

        for (OrderFromYouzanEntity orderFromYouzanEntity : orderFromYouzanEntities) {
            if ((orderFromYouzanEntity.getCreateTime().getTime() + (60 * 1000)) > System.currentTimeMillis()) {
                logger.debug("充值中的订单【" + orderFromYouzanEntity.getId() + "】，入库未够一分钟。");
                // 没入库够1分钟不理他
                continue;
            }
            WareFuluInfoEntity wareFuluInfoEntity = wareFuluInfoService.queryByOuterSkuId(orderFromYouzanEntity.getWareNo());
            logger.debug("充值中的订单【" + orderFromYouzanEntity.getId() + "】，查询充值状态");
            if (WareFuluInfoEntity.TYPE_NEW_RECHARGE_CHANNEL == wareFuluInfoEntity.getRechargeChannel()) {
                logger.debug("执行新版本查询");
                applicationContext.publishEvent(new FuluCheckV2Event(orderFromYouzanEntity.getId()));
                continue;
            }
            applicationContext.publishEvent(new FuluCheckEvent(orderFromYouzanEntity.getId()));
        }
    }
}
