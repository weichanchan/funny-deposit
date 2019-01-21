package com.funny.api.praise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.system.service.TokenService;
import com.funny.api.praise.entity.AccessToken;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 有赞订单通知
 *
 * @author liyanjun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 15:31
 */
@RestController
@RequestMapping("/api/praise/order")
public class ApiOrderNotifyController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    private ObjectMapper objectMapper = new ObjectMapper();

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
    public void notify(HttpServletRequest request) throws Exception {
        YouzanTradeMemoUpdateParams youzanTradeMemoUpdateParams = new YouzanTradeMemoUpdateParams();
        youzanTradeMemoUpdateParams.setTid("E20170607220305012000001");
        youzanTradeMemoUpdateParams.setMemo("墨鱼测试1111");

        YouzanTradeMemoUpdate youzanTradeMemoUpdate = new YouzanTradeMemoUpdate();
        youzanTradeMemoUpdate.setAPIParams(youzanTradeMemoUpdateParams);
        YouzanTradeMemoUpdateResult result = getClient().invoke(youzanTradeMemoUpdate);
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
