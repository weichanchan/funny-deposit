package com.funny.api.praise;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.service.CardInfoService;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.api.praise.entity.MsgPushEntity;
import com.funny.config.FuluConfig;
import com.funny.utils.SignUtils;
import com.funny.utils.annotation.IgnoreAuth;
import com.youzan.open.sdk.util.hash.MD5Utils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 福禄订单回调
 *
 * @author liyanjun
 */
@RestController
@RequestMapping("/api/fulu/order")
public class ApiFuluOrderNotifyController {

    private static final Logger logger = LoggerFactory.getLogger(ApiFuluOrderNotifyController.class);

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private FuluConfig fuluConfig;


    @IgnoreAuth
    @PostMapping("notify")
    public Object notify(HttpServletRequest request) {
        try {
            // 福禄订单号
            String orderNo = request.getParameter("OrderNo");
            // 交易完成时间
            String chargeTime = request.getParameter("ChargeTime");
            // 合作商家订单号
            String customerOrderNo = request.getParameter("CustomerOrderNo");
            // 订单状态(成功,失败)
            String status = request.getParameter("Status");
            String reMark = request.getParameter("ReMark");
            String sign = request.getParameter("Sign");

            Map<String, String> map = new HashMap(8);
            map.put("chargetime", chargeTime.replace("/", "-"));
            map.put("customerorderno", customerOrderNo);
            map.put("orderno", orderNo);
            map.put("remark", reMark);
            map.put("status", status);

            // 将字典集合转换为URL参数对
            String param = SignUtils.MaptoString(map);
            logger.debug("收到福禄订单回调通知：" + param);
            // 将秘钥拼接到URL参数对后
            String postData = param + fuluConfig.getKey();
            // 将请求参数进行MD5加密得到签名
            String temp = MD5Utils.MD5(postData);
            if (!temp.equals(sign)) {
                logger.debug("福禄订单回调通知验签失败：" + param + "【temp】:" + temp + "【sign】" + sign);
                return "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><ret><status>False</status></ret></root>";
            }

            OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.lockByOrderNo(customerOrderNo);
            if(orderFromYouzanEntity == null){
                throw new NullPointerException("找不到对应的订单。【customerOrderNo】" + customerOrderNo);
            }

            // 失败通知处理
            if ("False".equals(status)) {
                if (orderFromYouzanEntity.getStatus() == OrderFromYouzanEntity.SUCCESS) {
                    logger.debug("成功状态下的订单被通知失败。【id】" + orderFromYouzanEntity.getId());
                    orderFromYouzanEntity.setException("成功状态下的订单被通知失败。");
                }
                orderFromYouzanEntity.setException("主动通知充值失败");
                orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
                orderFromYouzanService.update(orderFromYouzanEntity);
                logger.debug("订单福禄平台充值失败。【id】" + orderFromYouzanEntity.getId());
                return "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><ret><status>True</status></ret></root>";
            }

            // 成功通知处理

            if (orderFromYouzanEntity.getStatus() == OrderFromYouzanEntity.REFUND_SUCCESS) {
                logger.debug("已经置为失败的订单，但福禄通知充值成功。【id】" + orderFromYouzanEntity.getId());
                orderFromYouzanEntity.setException("已经退款订单，但福禄通知成功。");
            }
            if (orderFromYouzanEntity.getStatus() == OrderFromYouzanEntity.FAIL) {
                logger.debug("已经置为失败的订单，但福禄通知充值成功。【id】" + orderFromYouzanEntity.getId());
                orderFromYouzanEntity.setException("已经置为失败的订单，但福禄通知充值成功。");
            }
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.SUCCESS);
            orderFromYouzanService.update(orderFromYouzanEntity);
            logger.debug("订单福禄平台充值成功。【id】" + orderFromYouzanEntity.getId());
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><ret><status>True</status></ret></root>";
        } catch (Exception e) {
            logger.error("接受福禄平台回调失败", e);
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><ret><status>False</status></ret></root>";
        }
    }


}
