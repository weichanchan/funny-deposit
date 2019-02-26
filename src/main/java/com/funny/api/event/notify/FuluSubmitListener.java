package com.funny.api.event.notify;

import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.config.FuluConfig;
import com.funny.utils.SignUtils;
import com.youzan.open.sdk.util.hash.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.UnknownHostException;
import java.util.Map;

/**
 * 去福禄平台下单监听器
 *
 * @author liyanjun
 */
@Component
public class FuluSubmitListener {

    private static final Logger logger = LoggerFactory.getLogger(FuluSubmitListener.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private WareInfoService wareInfoService;

    @Autowired
    private FuluConfig fuluConfig;

    @Async
    @EventListener
    public void onApplicationEvent(FuluSubmitEvent fuluSubmitEvent) throws UnknownHostException {
        Integer id = Integer.parseInt(String.valueOf(fuluSubmitEvent.getSource()));
        OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
        // 不是待充值状态，不处理
        if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.WATI_PROCESS) {
            return;
        }
        WareInfoEntity wareInfoEntity = wareInfoService.queryObjectByWareNo(orderFromYouzanEntity.getWareNo());
        if (wareInfoEntity == null) {
            //TODO 没有这个商品要退款
        }

        Map map = SignUtils.getFuluHeader("kamenwang.order.add");
        // 业务参数
        // 合作商家订单号（唯一不重复）
        map.put("customerorderno", orderFromYouzanEntity.getOrderNo());
        // 福禄商品编号
        map.put("productid", "1204405");
        // 用户编号
        map.put("customerid", "803683");
        // 购买数量
        map.put("buynum", "1");
        // 充值账号
        map.put("chargeaccount", "123456");
        // 提交订单的回调地址
        map.put("notifyurl", "https://www.lckj.shop/api/praise/order/notify");
        String param = SignUtils.MaptoString(map);
        // 将秘钥拼接到URL参数对后
        String postData = param + fuluConfig.getKey();
        String sign = MD5Utils.MD5(postData);
        // 将签名添加到URL参数后
        param = param + "&sign=" + sign.toUpperCase();

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(fuluConfig.getUrl() + "?" + param, null, Map.class);
        if (map.get("MessageCode") == null) {
            // 失败,记录异常，退款
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
        }
        // 福禄平台已经受理订单
        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.PROCESS);
    }
}
