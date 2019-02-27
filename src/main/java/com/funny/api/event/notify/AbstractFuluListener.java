package com.funny.api.event.notify;

import com.funny.utils.SignUtils;
import com.youzan.open.sdk.util.hash.MD5Utils;

import java.util.Map;

/**
 * @author liyanjun
 */

public abstract class AbstractFuluListener {
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
        String postData = param + getUrl();
        String sign = MD5Utils.MD5(postData);
        // 将签名添加到URL参数后
        String request = getUrl() + "?" + param + "&sign=" + sign.toUpperCase();
        return request;
    }

    public abstract String getUrl();
}
