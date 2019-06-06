package com.funny.api.praise;

import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.config.FuluConfig;
import com.funny.utils.SignUtils;
import com.funny.utils.annotation.IgnoreAuth;
import com.youzan.open.sdk.util.hash.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * A订单回调
 *
 * @author liyanjun
 */
@RestController
@RequestMapping("/api/a/order")
public class ANotifyController {

    private static final Logger logger = LoggerFactory.getLogger(ANotifyController.class);

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private FuluConfig fuluConfig;


    @IgnoreAuth
    @PostMapping("notify")
    public String notify(HttpServletRequest request) {
        try {
            // A平台订单号
            String outOrderNo = request.getParameter("outOrderNo");
            String status = request.getParameter("status");
            OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.lockByOrderNo(outOrderNo);
            if (orderFromYouzanEntity == null) {
                throw new NullPointerException("找不到对应的订单。【outOrderNo】" + outOrderNo);
            }

            // 失败通知处理
            if ("FAIL".equals(status)) {
                if (orderFromYouzanEntity.getStatus() == OrderFromYouzanEntity.SUCCESS) {
                    logger.debug("成功状态下的订单被通知失败。【id】" + orderFromYouzanEntity.getId());
                    orderFromYouzanEntity.setException("成功状态下的订单被通知失败。");
                } else {
                    orderFromYouzanEntity.setException("主动通知充值失败");
                }
                logger.debug("订单福禄平台充值失败。【id】" + orderFromYouzanEntity.getId());
                orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
                orderFromYouzanService.update(orderFromYouzanEntity);

            }

            // 成功通知处理
            if (orderFromYouzanEntity.getStatus() == OrderFromYouzanEntity.REFUND_SUCCESS) {
                logger.debug("已经置为失败的订单，但A平台通知充值成功。【id】" + orderFromYouzanEntity.getId());
                orderFromYouzanEntity.setException("已经退款订单，但A通知成功。");
            }
            if (orderFromYouzanEntity.getStatus() == OrderFromYouzanEntity.FAIL) {
                logger.debug("已经置为失败的订单，但A平台通知充值成功。【id】" + orderFromYouzanEntity.getId());
                orderFromYouzanEntity.setException("已经置为失败的订单，但A平台通知充值成功。");
            }
            logger.debug("订单A平台充值成功。【id】" + orderFromYouzanEntity.getId());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.SUCCESS);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return "SUCCESS";
        } catch (Exception e) {
            return "FAIL";
        }


    }


}
