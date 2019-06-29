package com.funny.api.praise;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.CardInfoService;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.api.event.notify.FuluSubmitEvent;
import com.funny.api.event.notify.YouzanRefundEvent;
import com.funny.api.event.notify.a.ASubmitEvent;
import com.funny.api.event.notify.superman.SupermanSubmitEvent;
import com.funny.api.event.notify.v2.FuluSubmitV2Event;
import com.funny.api.praise.entity.MsgPushEntity;
import com.funny.utils.annotation.IgnoreAuth;
import com.youzan.open.sdk.util.hash.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 有赞订单通知
 *
 * @author liyanjun
 */
@RestController
@RequestMapping("/api/praise/order")
@ConditionalOnProperty("optional.youzan.enable")
public class ApiOrderNotifyController {

    private static final Logger logger = LoggerFactory.getLogger(ApiOrderNotifyController.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WareFuluInfoService wareFuluInfoService;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Value("${optional.youzan.client-id}")
    private String clientId;

    @Value("${optional.youzan.client-secret}")
    private String clientSecret;

    @Value("${optional.youzan.ktd-id}")
    private String ktdId;

    /**
     * 登录
     */
    @IgnoreAuth
    @PostMapping("notify")
    public Object notify(@RequestBody MsgPushEntity entity) throws Exception {
        JSONObject res = new JSONObject();
        res.put("code", 0);
        res.put("msg", "success");
        /**
         *  判断是否为心跳检查消息，1.是则直接返回
         */
        if (entity.isTest()) {
            logger.error("心跳请求，不处理。");
            return res;
        }
        /**
         * 检查是否是付款推送
         */
        if (!("trade_TradePaid".equals(entity.getType()) && "TRADE_PAID".equals(entity.getStatus()))) {
            logger.debug("不是付款请求不用处理。");
            return res;
        }
        /**
         * 判断消息是否合法
         * md5方法可参考 https://www.youzanyun.com/support/faq/4215?qa_id=13065
         */
        String sign = MD5Utils.MD5(clientId + entity.getMsg() + clientSecret);
        logger.debug(sign);
//        if (!sign.equals(entity.getSign())) {
//            logger.error("MSG" + entity.getSign() + "签名不正确。");
//            return res;
//        }
        // 获取订单信息
        String msg = URLDecoder.decode(entity.getMsg(), "utf-8");
        logger.debug(msg);
        Map result = objectMapper.readValue(msg, Map.class);
        // 总体信息
        Map fullOrderInfo = (Map) result.get("full_order_info");
        // 订单信息
        Map orderInfo = (Map) fullOrderInfo.get("order_info");
        // 商品信息
        List<Map<String, Object>> orders = (List) fullOrderInfo.get("orders");
        if (orders.size() > 1) {
            logger.error("不支持多个订单。");
            return res;
        }
        Map<String, Object> order = orders.get(0);
        String outerSkuId = (String) order.get("outer_sku_id");
        if (outerSkuId == null) {
            logger.debug("outerSkuId未配置，不用处理");
            return res;
        }
        WareFuluInfoEntity wareFuluInfoEntity = wareFuluInfoService.queryByOuterSkuId(outerSkuId);
        if (wareFuluInfoEntity == null) {
            logger.debug("outerSkuId在系统中找不到，不用处理");
            return res;
        }
        // 订单ID
        String tid = (String) orderInfo.get("tid");

        OrderFromYouzanEntity orderFromYouzanEntity = new OrderFromYouzanEntity();
        orderFromYouzanEntity.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        orderFromYouzanEntity.setYouzanOrderId(tid);
        orderFromYouzanEntity.setWareNo(outerSkuId);
        // 规格信息
        orderFromYouzanEntity.setFormatInfo(String.valueOf(order.get("sku_properties_name")));
        // 充值号码备注信息
        orderFromYouzanEntity.setRechargeInfo(String.valueOf(order.get("buyer_messages")));
        // 子订单ID
        orderFromYouzanEntity.setSubOrderId(String.valueOf(order.get("oid")));
        // 订单金额
        orderFromYouzanEntity.setOrderPrice(new BigDecimal(String.valueOf(order.get("total_fee"))));
        // 订单数量
        orderFromYouzanEntity.setNum(Integer.parseInt(String.valueOf(order.get("num"))));
        // 订单状态为处理中
        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.WAIT_PROCESS);
        orderFromYouzanEntity.setCreateTime(new Date());
        orderFromYouzanService.save(orderFromYouzanEntity);
        if (WareFuluInfoEntity.TYPE_A_CHANNEL == wareFuluInfoEntity.getRechargeChannel()) {
            applicationContext.publishEvent(new ASubmitEvent(orderFromYouzanEntity.getId()));
            return res;
        }
        if (WareFuluInfoEntity.TYPE_NEW_RECHARGE_CHANNEL == wareFuluInfoEntity.getRechargeChannel()) {
            applicationContext.publishEvent(new FuluSubmitV2Event(orderFromYouzanEntity.getId()));
            return res;
        }
        if (WareFuluInfoEntity.TYPE_SUPERMAN_CHANNEL == wareFuluInfoEntity.getRechargeChannel()) {
            if (wareFuluInfoEntity.getWareName().contains("Q币")) {
                applicationContext.publishEvent(new SupermanSubmitEvent(orderFromYouzanEntity.getId()));
            } else {
                // 非Q币的超人渠道，如果要购买多个，需要拆单
                Integer num = wareFuluInfoEntity.getNum() * orderFromYouzanEntity.getNum();
                for (int i = 1; i < num ; i++) {
                    OrderFromYouzanEntity orderFromYouzanEntity1 = new OrderFromYouzanEntity();
                    BeanUtils.copyProperties(orderFromYouzanEntity,orderFromYouzanEntity1);
                    orderFromYouzanEntity1.setOrderNo(orderFromYouzanEntity.getOrderNo() + "-" + i);
                    orderFromYouzanEntity1.setYouzanOrderId(orderFromYouzanEntity.getYouzanOrderId() + "-" + i);
                    orderFromYouzanEntity1.setSubOrderId(orderFromYouzanEntity.getSubOrderId() + "-" + i);
                    orderFromYouzanEntity1.setLastRechargeTime(new Date(orderFromYouzanEntity.getCreateTime().getTime() + (60000 * i)));
                    orderFromYouzanService.save(orderFromYouzanEntity1);
                }
                applicationContext.publishEvent(new SupermanSubmitEvent(orderFromYouzanEntity.getId()));
            }
            return res;
        }
        applicationContext.publishEvent(new FuluSubmitEvent(orderFromYouzanEntity.getId()));
        return res;
    }

}
