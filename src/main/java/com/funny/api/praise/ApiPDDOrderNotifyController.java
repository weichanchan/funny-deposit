package com.funny.api.praise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.api.event.notify.FuluSubmitEvent;
import com.funny.api.event.notify.a.ASubmitEvent;
import com.funny.api.event.notify.superman.SupermanSubmitEvent;
import com.funny.api.event.notify.v2.FuluSubmitV2Event;
import com.funny.config.AConfig;
import com.funny.utils.R;
import com.funny.utils.SignUtils;
import com.funny.utils.annotation.IgnoreAuth;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

/**
 * 有赞订单通知
 *
 * @author liyanjun
 */
@RestController
@RequestMapping("/api/pdd/order")
public class ApiPDDOrderNotifyController {

    private static final Logger logger = LoggerFactory.getLogger(ApiPDDOrderNotifyController.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WareFuluInfoService wareFuluInfoService;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;
    @Autowired
    private AConfig aConfig;

    @IgnoreAuth
    @PostMapping("check")
    public Object check(String outerSkuId) {
        WareFuluInfoEntity wareFuluInfoEntity = wareFuluInfoService.queryByOuterSkuId(outerSkuId);
        if (wareFuluInfoEntity == null) {
            return R.error("检查商品编号【" + outerSkuId + "】不需要自动充值");
        }
        return R.ok();
    }

    /**
     * 拼多多通知
     */
    @IgnoreAuth
    @PostMapping("notify")
    public Object notify(String orderId, String number, String outerSkuId, Integer count, Long timeStamp, String sign) throws IOException {
        String paramToDecode = "number=" + URLEncoder.encode(number, "UTF-8") + "&orderId=" + orderId + "&count=" + count + "&timeStamp=" + timeStamp + "&outerSkuId=" + URLEncoder.encode(outerSkuId, "UTF-8");
        String tempSign = SignUtils.getMD5(paramToDecode + aConfig.getClientKey());
        if (!sign.equals(tempSign)) {
            logger.debug(paramToDecode);
            logger.debug(tempSign);
            logger.debug(sign);
            logger.debug("签名错误");
            return R.ok();
        }
        logger.debug("number=" + number + "&orderId=" + orderId + "&count=" + count + "&timeStamp=" + timeStamp + "&outerSkuId=" + outerSkuId);
        // 获取订单信息
        if (outerSkuId == null) {
            logger.debug("outerSkuId未配置，不用处理");
            return R.ok();
        }
        WareFuluInfoEntity wareFuluInfoEntity = wareFuluInfoService.queryByOuterSkuId(outerSkuId);
        if (wareFuluInfoEntity == null) {
            logger.debug("outerSkuId在系统中找不到，不用处理");
            return R.ok();
        }
        // 订单ID

        OrderFromYouzanEntity orderFromYouzanEntity = new OrderFromYouzanEntity();
        orderFromYouzanEntity.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        orderFromYouzanEntity.setYouzanOrderId(orderId);
        orderFromYouzanEntity.setWareNo(outerSkuId);
        // 规格信息
        orderFromYouzanEntity.setFormatInfo(outerSkuId);
        // 充值号码备注信息
        orderFromYouzanEntity.setRechargeInfo(number);
        // 订单金额
        orderFromYouzanEntity.setOrderPrice(BigDecimal.ZERO);
        // 订单数量
        orderFromYouzanEntity.setNum(count);
        // 订单状态为处理中
        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.WAIT_PROCESS);
        orderFromYouzanEntity.setCreateTime(new Date());
        orderFromYouzanEntity.setRechargeChannel(wareFuluInfoEntity.getRechargeChannel());
        orderFromYouzanService.save(orderFromYouzanEntity);
        if (WareFuluInfoEntity.TYPE_A_CHANNEL == wareFuluInfoEntity.getRechargeChannel()) {
            applicationContext.publishEvent(new ASubmitEvent(orderFromYouzanEntity.getId()));
            return R.ok();
        }
        if (WareFuluInfoEntity.TYPE_NEW_RECHARGE_CHANNEL == wareFuluInfoEntity.getRechargeChannel()) {
            applicationContext.publishEvent(new FuluSubmitV2Event(orderFromYouzanEntity.getId()));
            return R.ok();
        }
        if (WareFuluInfoEntity.TYPE_SUPERMAN_CHANNEL == wareFuluInfoEntity.getRechargeChannel()) {
            if (wareFuluInfoEntity.getWareName().contains("Q币") || wareFuluInfoEntity.getWareName().contains("喜点")) {
                applicationContext.publishEvent(new SupermanSubmitEvent(orderFromYouzanEntity.getId()));
            } else {
                // 非Q币的超人渠道，如果要购买多个，需要拆单
                Integer num = wareFuluInfoEntity.getNum() * orderFromYouzanEntity.getNum();
                for (int i = 1; i < num; i++) {
                    OrderFromYouzanEntity orderFromYouzanEntity1 = new OrderFromYouzanEntity();
                    BeanUtils.copyProperties(orderFromYouzanEntity, orderFromYouzanEntity1);
                    orderFromYouzanEntity1.setOrderNo(orderFromYouzanEntity.getOrderNo() + "-" + i);
                    orderFromYouzanEntity1.setYouzanOrderId(orderFromYouzanEntity.getYouzanOrderId() + "-" + i);
                    orderFromYouzanEntity1.setLastRechargeTime(new Date(orderFromYouzanEntity.getCreateTime().getTime() + (60000 * i)));
                    orderFromYouzanService.save(orderFromYouzanEntity1);
                }
                applicationContext.publishEvent(new SupermanSubmitEvent(orderFromYouzanEntity.getId()));
            }
            return R.ok();
        }
        applicationContext.publishEvent(new FuluSubmitEvent(orderFromYouzanEntity.getId()));
        return R.ok();
    }

}
