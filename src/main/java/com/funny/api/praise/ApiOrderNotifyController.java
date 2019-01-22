package com.funny.api.praise;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.system.service.TokenService;
import com.funny.api.praise.entity.AccessToken;
import com.funny.api.praise.entity.MsgPushEntity;
import com.funny.utils.R;
import com.funny.utils.annotation.IgnoreAuth;
import com.funny.admin.system.service.UserService;
import com.funny.utils.validator.Assert;
import com.youzan.open.sdk.client.auth.Auth;
import com.youzan.open.sdk.client.auth.Token;
import com.youzan.open.sdk.client.core.DefaultYZClient;
import com.youzan.open.sdk.client.core.YZClient;
import com.youzan.open.sdk.gen.v3_0_0.api.YouzanTradeMemoUpdate;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanTradeMemoUpdateParams;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanTradeMemoUpdateResult;
import com.youzan.open.sdk.model.AuthType;
import com.youzan.open.sdk.util.hash.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 有赞订单通知
 *
 * @author liyanjun
 */
@RestController
@RequestMapping("/api/praise/order")
public class ApiOrderNotifyController {

    private static final Logger logger = LoggerFactory.getLogger(ApiOrderNotifyController.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final int mode = 1; //服务商

    @Autowired
    RestTemplate template;

    @Value("${optional.youzan.client-id}")
    private String clientId;

    @Value("${optional.youzan.client-secret}")
    private String clientSecret;

    @Value("${optional.youzan.ktd-id}")
    private String ktdId;

    private YZClient client;

    private AccessToken accessToken;

    /**
     * 登录
     */
    @IgnoreAuth
    @PostMapping("notify")
    public Object notify(@RequestBody MsgPushEntity entity) throws Exception {
        logger.debug("**************begin praise notify test: " + entity.isTest() + "mode" + entity.getMode() + "**************");
        JSONObject res = new JSONObject();
        res.put("code", 0);
        res.put("msg", "success");
        /**
         *  判断是否为心跳检查消息，1.是则直接返回
         */
        if (entity.isTest()) {
            return res;
        }
        /**
         * 解析消息推送的模式  这步判断可以省略
         * 0-商家自由消息推送 1-服务商消息推送
         * 以服务商举例,判断是否为服务商类型的消息,否则直接返回
         */
        if (entity.getMode() != mode) {
            return res;
        }
        /**
         * 判断消息是否合法
         * md5方法可参考 https://www.youzanyun.com/support/faq/4215?qa_id=13065
         */
        String sign = MD5Utils.MD5(clientId + entity.getMsg() + clientSecret);
        if (!sign.equals(entity.getSign())) {
            logger.debug("MSG" + entity.getSign() + "签名不正确。");
            return res;
        }
        /**
         *  接下来是一些业务处理
         *  判断当前消息的类型 比如交易
         */
        logger.debug("交易类型 TradeType:" + entity.getSign());
        String msg = URLDecoder.decode(entity.getMsg(), "utf-8");
        logger.debug(msg);
        if ("trade_TradeBuyerPay".equals(entity.getType())) {
//            // 可以优化成消息模式
//            // 修改备注添加卡密信息
//            String msg = URLDecoder.decode(entity.getMsg(), "utf-8");
//            Map result = objectMapper.readValue(msg, Map.class);
//            Map fullOrderInfo = (Map) result.get("full_order_info");
//            Map orderInfo = (Map) fullOrderInfo.get("order_info");
//
//            YouzanTradeMemoUpdateParams youzanTradeMemoUpdateParams = new YouzanTradeMemoUpdateParams();
//            youzanTradeMemoUpdateParams.setTid((String) orderInfo.get("tid"));
//            youzanTradeMemoUpdateParams.setMemo("墨鱼测试1111");
//
//            YouzanTradeMemoUpdate youzanTradeMemoUpdate = new YouzanTradeMemoUpdate();
//            youzanTradeMemoUpdate.setAPIParams(youzanTradeMemoUpdateParams);
//            YouzanTradeMemoUpdateResult requestResult = getClient().invoke(youzanTradeMemoUpdate);
        }

        logger.debug("**************end praise notify**************");
        return res;
    }

    /**
     * 获取有赞 token 用于通讯
     *
     * @return
     *
     * @throws IOException
     */
    private AccessToken getToken() throws IOException {
        ResponseEntity<String> response = template.postForEntity("https://open.youzan.com/oauth/token?client_id=" + clientId + "&client_secret=t" + clientSecret + "&grant_type=silent&kdt_id=" + ktdId, null, String.class);
        Map result = objectMapper.readValue(response.getBody(), Map.class);
        String accessToken = (String) result.get("access_token");
        Long expiresIn = (Long) result.get("expires_in");
        // 提前过期时间1天刷新Token
        return new AccessToken(accessToken, System.currentTimeMillis() + ((expiresIn - 3600 * 24) * 1000));
    }

    /**
     * 使用有赞 client 前的一些检查
     *
     * @return
     *
     * @throws IOException
     */
    private YZClient getClient() throws IOException {

        if (accessToken == null || accessToken.isExpire()) {
            accessToken = getToken();
        }

        if (client == null) {
            client = new DefaultYZClient(new Token(accessToken.getAccessToken()));
        }

        return client;
    }

}
