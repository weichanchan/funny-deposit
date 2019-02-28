package com.funny.api.event.notify;

import com.funny.config.FuluConfig;
import com.funny.utils.DateUtils;
import com.funny.utils.SignUtils;
import com.youzan.open.sdk.util.hash.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyanjun
 */

public abstract class AbstractFuluListener {

    @Autowired
    protected FuluConfig fuluConfig;

    /**
     * 拼参数，签名
     *
     * @param map
     *
     * @return
     */
    public String getRequestString(Map map) {
        String param = SignUtils.MaptoString(map);
        // 将秘钥拼接到URL参数对后
        String postData = param + getSecret();
        String sign = MD5Utils.MD5(postData);
        // 将签名添加到URL参数后
        String request = getUrl() + "?" + param + "&sign=" + sign.toUpperCase();
        return request;
    }

    public static Map getFuluHeader(String type){
        Map map = new HashMap(8);
        map.put("method", type);
        map.put("timestamp", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        map.put("format", "json");
        map.put("v", "1.0");
        return map;
    }

    public String getUrl() {
        return fuluConfig.getUrl();
    }

    public String getSecret() {
        return fuluConfig.getKey();
    }
}
