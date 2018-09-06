package com.funny.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 签名校验工具
 */
public class SignUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(SignUtils.class);

    //秘钥
    private static final String secretKeyOfFunny = "PRIVATEKEY";
    //版本号
    private static final String versionNo = "1.0";
    private static final String appidOfWxh = "xxx";

    public static Boolean checkSign(Map params) {
        Boolean flag = false;
        String sign = (String) params.get("sign");
        String timestamp = (String) params.get("timestamp");
        String version = (String) params.get("version");
        //check时间戳的值是否在当前时间戳前后一小时以内
        String currTimestamp = String.valueOf(new Date().getTime() / 1000); // 当前时间的时间戳
        Long currTimestampNum = Long.parseLong(currTimestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Long verifyTimestampNum = null; // 时间戳的数值
        try {
            verifyTimestampNum = simpleDateFormat.parse(timestamp).getTime() / 1000;
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        // 在一小时范围之外，访问已过期
        if (Math.abs(verifyTimestampNum - currTimestampNum) < 600) {
            //检查sigin是否过期
            Map<String, Object> param = new HashMap<String, Object>();
            for (Iterator it = params.keySet().iterator(); it.hasNext(); ) {
                String key = it.next().toString();
                //参数为空不参与签名
                if (StringUtils.isNotEmpty((String) params.get(key))) {
                    param.put(key, params.get(key));
                }
            }
            param.remove("sign");
            param.remove("signType");
            String newSign = getSign(param, secretKeyOfFunny);
            if (sign.equals(newSign) && version.equals(versionNo)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * MD5 32位小写加密
     *
     * @param str
     * @return
     * @throws IOException
     */
    public static String getMD5(String str) throws IOException {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String md5 = new BigInteger(1, md.digest()).toString(16);
            //BigInteger会把0省略掉，需补全至32位
            return fillMD5(md5);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密错误:" + e.getMessage(), e);
        }

    }

    public static String fillMD5(String md5) {
        return md5.length() == 32 ? md5 : fillMD5("0" + md5);
    }

    /**
     * 得到签名
     *
     * @param params    参数集合不含secretkey
     * @param secretkey 验证接口的secretkey
     * @return
     */
    public static String getSign(Map<String, Object> params, String secretkey) {
        String sign = "";
        StringBuilder sb = new StringBuilder();
        //step1：先对请求参数排序
        Set<String> keyset = params.keySet();
        TreeSet<String> sortSet = new TreeSet<String>();
        sortSet.addAll(keyset);
        Iterator<String> it = sortSet.iterator();
        //step2：把参数的key value链接起来 secretkey放在最后面，得到要加密的字符串
        while (it.hasNext()) {
            String key = it.next();
            Object value = params.get(key);
            sb.append(key).append(value);
        }
        sb.append(secretkey);
        try {
            //Md5加密得到sign
            sign = getMD5(sb.toString());
        } catch (IOException e) {
            LOGGER.error("生成签名错误", e);
        }
        return sign;
    }
}
