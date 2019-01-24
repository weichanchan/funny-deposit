package com.funny.api.praise;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.service.CardInfoService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.api.event.notify.YouzanNotifyEvent;
import com.funny.api.praise.entity.MsgPushEntity;
import com.funny.api.praise.entity.YouzanNotifyEventSource;
import com.funny.utils.annotation.IgnoreAuth;
import com.youzan.open.sdk.util.hash.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

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
    ApplicationContext applicationContext;

    @Autowired
    private WareInfoService wareInfoService;

    @Autowired
    private CardInfoService cardInfoService;

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
        logger.debug("**************begin praise notify**************");
        logger.debug(objectMapper.writeValueAsString(entity));
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
        if (!sign.equals(entity.getSign())) {
            logger.error("MSG" + entity.getSign() + "签名不正确。");
            return res;
        }
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
        // 订单ID
        String tid = (String) orderInfo.get("tid");
        String cardInfoString = wareInfoService.getPasscode(entity, orderInfo, orders);
        if (StringUtils.isBlank(cardInfoString)) {
            //TODO 没卡密了，登记一下错误
            logger.error("卡密库存不足。");
            return res;
        }
        logger.debug(tid);
        logger.debug(cardInfoString);
        // TODO 记录一个有赞订单
        applicationContext.publishEvent(new YouzanNotifyEvent(new YouzanNotifyEventSource(tid, cardInfoString)));
        return res;
    }

}