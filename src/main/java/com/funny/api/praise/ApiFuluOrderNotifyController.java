package com.funny.api.praise;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.service.CardInfoService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.api.praise.entity.MsgPushEntity;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 登录
     */
    @IgnoreAuth
    @PostMapping("notify")
    public Object notify(HttpServletRequest request) {
        return null;
    }

}
