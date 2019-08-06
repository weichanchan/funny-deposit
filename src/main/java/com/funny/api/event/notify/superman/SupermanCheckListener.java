package com.funny.api.event.notify.superman;

import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.ThridPlatformGateEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.ThridPlatformGateService;
import com.funny.config.SupermanConfig;
import com.funny.utils.SignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * 去超人平台下单监听器
 *
 * @author liyanjun
 */
@Component
public class SupermanCheckListener {

    private static final Logger logger = LoggerFactory.getLogger(SupermanCheckListener.class);

    private static final int gate = 3;

    @Autowired
    protected SupermanConfig supermanConfig;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    private ThridPlatformGateService thridPlatformGateService;

    @Async
    @EventListener
    public void onApplicationEvent(SupermanCheckEvent supermanCheckEvent) throws Exception {
        Integer id = Integer.parseInt(String.valueOf(supermanCheckEvent.getSource()));
        OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
        // 不是充值中状态，不处理
        if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.PROCESS) {
            return;
        }
        if (orderFromYouzanEntity.getNextRechargeTime() != null && orderFromYouzanEntity.getNextRechargeTime().after(new Date())) {
            // 还没到查询时间
            return;
        }
        ThridPlatformGateEntity thridPlatformGateEntity = thridPlatformGateService.queryObject(gate);
        if (thridPlatformGateEntity.getCheckStatus() == ThridPlatformGateEntity.STATUS_CLOSE) {
            logger.debug("超人平台渠道关闭，先不查询");
            return;
        }
        MultiValueMap map = new LinkedMultiValueMap(16);
        // 合作商家订单号（唯一不重复）
        map.put("user", Collections.singletonList(supermanConfig.getUsername()));
        map.put("pass", Collections.singletonList(SignUtils.getMD5(supermanConfig.getPassword())));
        if (orderFromYouzanEntity.getOrderNo().split("---").length <= 1) {
            orderFromYouzanEntity.setException("请到第三方充值平台确认充值状态");
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.CHECK_FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }
        map.put("order", Collections.singletonList(orderFromYouzanEntity.getOrderNo().split("---")[1]));
        map.put("code", Collections.singletonList("1010"));
        map.put("token", Collections.singletonList(supermanConfig.getToken()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        // 将签名添加到URL参数后
        try {
            ListenableFuture<ResponseEntity<Map>> forEntity = asyncRestTemplate.postForEntity(supermanConfig.getUrl(), request, Map.class);
            forEntity.addCallback(new ListenableFutureCallback<ResponseEntity<Map>>() {
                @Override
                public void onFailure(Throwable throwable) {
                    recordCheckFail(orderFromYouzanEntity);
                }

                @Override
                public void onSuccess(ResponseEntity<Map> responseEntity) {
                    //创建一个DocumentBuilderFactory的对象
                    logger.debug(responseEntity.getBody().toString());
                    // 受理成功和处理中先不管，失败就置为失败
                    if ("0".equals(responseEntity.getBody().get("study").toString())) {
                        orderFromYouzanEntity.setException("充值失败：超人平台通知失败");
                        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
                        orderFromYouzanService.update(orderFromYouzanEntity);
                        return;
                    }

                    if ("5".equals(responseEntity.getBody().get("stduy").toString())) {
                        orderFromYouzanEntity.setException("充值失败：超人平台通知失败，超人平台已退款。");
                        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
                        orderFromYouzanService.update(orderFromYouzanEntity);
                        return;
                    }

                    if ("6".equals(responseEntity.getBody().get("stduy").toString())) {
                        orderFromYouzanEntity.setException("充值失败：超人平台通知失败，超人平台已取消");
                        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
                        orderFromYouzanService.update(orderFromYouzanEntity);
                        return;
                    }

                    if ("4".equals(responseEntity.getBody().get("stduy").toString())) {
                        // 已发货
                        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.SUCCESS);
                        orderFromYouzanService.update(orderFromYouzanEntity);
                        return;
                    }

                    recordCheckFail(orderFromYouzanEntity);
                }
            });
        } catch (Exception e) {
            recordCheckFail(orderFromYouzanEntity);
        }

    }

    private void recordCheckFail(OrderFromYouzanEntity orderFromYouzanEntity) {
        logger.debug("订单【" + orderFromYouzanEntity.getId() + "】查询失败，检查是否重试");
        int count = orderFromYouzanEntity.getCount() + 1;
        if (count >= 5) {
            logger.debug("订单【" + orderFromYouzanEntity.getId() + "】重试到达上线");
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.CHECK_FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }
        orderFromYouzanEntity.setCount(count);
        orderFromYouzanEntity.setNextRechargeTime(new Date(orderFromYouzanEntity.getCreateTime().getTime() + count * count * 60 * 1000));
        logger.debug("订单【" + orderFromYouzanEntity.getId() + "】需要重试，下次重试时间" + orderFromYouzanEntity.getNextRechargeTime());
        orderFromYouzanService.update(orderFromYouzanEntity);
    }

}
