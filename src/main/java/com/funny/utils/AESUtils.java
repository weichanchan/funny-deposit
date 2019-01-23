package com.funny.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.api.praise.entity.MsgPushEntity;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AES加密解密工具类
 *
 * @author M-Y
 */
public class AESUtils {
    private static final Logger logger = Logger.getLogger(AESUtils.class);
    private static final String defaultCharset = "UTF-8";
    private static final String KEY_AES = "AES";
    private static final String KEY = "123456";

    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key  加密密码
     *
     * @return
     */
    public static String encrypt(String data, String key) {
        return doAES(data, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     *
     * @param data 待解密内容
     * @param key  解密密钥
     *
     * @return
     */
    public static String decrypt(String data, String key) {
        return doAES(data, key, Cipher.DECRYPT_MODE);
    }

    /**
     * 加解密
     *
     * @param data 待处理数据
     * @param key  密钥
     * @param mode 加解密mode
     *
     * @return
     */
    private static String doAES(String data, String key, int mode) {
        try {
            if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
                return null;
            }
            //判断是加密还是解密
            boolean encrypt = mode == Cipher.ENCRYPT_MODE;
            byte[] content;
            //true 加密内容 false 解密内容
            if (encrypt) {
                content = data.getBytes(defaultCharset);
            } else {
                content = parseHexStr2Byte(data);
            }
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            kgen.init(128, new SecureRandom(key.getBytes()));
            //加密没关系，SecureRandom是生成安全随机数序列，key.getBytes()是种子，
            // 只要种子相同，序列就一样，所以解密只要有password就行
            //3.产生原始对称密钥
            SecretKey secretKey = kgen.generateKey();
            //4.获得原始对称密钥的字节数组
            //返回基本编码格式的密钥，如果此密钥不支持编码，则返回null。
            byte[] enCodeFormat = secretKey.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, KEY_AES);
            //6.根据指定算法AES自成密码器
            // 创建密码器
            Cipher cipher = Cipher.getInstance(KEY_AES);
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(mode, keySpec);
            //加密
            byte[] result = cipher.doFinal(content);
            return Base64.encodeBase64String(result);//通过Base64转码返回
 /*if (encrypt) {
  //将二进制转换成16进制
  return parseByte2HexStr(result);
 } else {
  return new String(result, defaultCharset);
 }*/
        } catch (Exception e) {
            logger.error("AES 密文处理异常", e);
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     *
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     *
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String content = "{'repairPhone':'18547854787','customPhone':'12365478965','captchav':'58m7'}";
        System.out.println("加密前：" + content);
        System.out.println("加密密钥和解密密钥：" + KEY);
        String encrypt = encrypt(content, KEY);
        System.out.println("加密后：" + encrypt);
        String decrypt = decrypt(encrypt, KEY);
        System.out.println("解密后：" + decrypt);

        ObjectMapper objectMapper = new ObjectMapper();
        MsgPushEntity msgPushEntity = new MsgPushEntity();
        msgPushEntity.setMsg("{" +
                "  \"full_order_info\": {" +
                "\"orders\": [" +
                "{" +
                " \"title\": \"宜家水杯\"," +
                " \"num\": 100," +
                " \"price\": \"100.00\"," +
                " \"payment\": \"90.00\"," +
                " \"item_type\": 0," +
                " \"outer_item_id\": \"ABC\"," +
                " \"outer_sku_id\": \"13323\"," +
                " \"pic_path\": \"https://img.com\"," +
                " \"sku_properties_name\": \"白色\"," +
                " \"buyer_messages\": \"及时快递发货\"," +
                " \"alias\": \"3f40ohzie8yfa\"," +
                " \"points_price\": \"0\"," +
                " \"oid\": 123456," +
                " \"sku_id\": 123," +
                " \"is_present\": \"false\"," +
                " \"total_fee\": \"90.00\"," +
                " \"item_id\": 123456," +
                " \"goods_url\": \"https://h5.youzan.com/v2/goods/3f40ohzie8yfa\"" +
                "}" +
                "]," +
                "\"order_info\": {" +
                "\"status\": \"WAIT_BUYER_PAY\"," +
                "\"type\": 0," +
                "\"tid\": \"E20180101111112340001\"," +
                "\"status_str\": \"已付款\"," +
                "\"pay_type\": 0," +
                "\"close_type\": 0," +
                "\"expired_time\": \"2018-01-01 00:00:00\"," +
                "\"express_type\": 0," +
                "\"refund_state\": 0," +
                "\"team_type\": 0," +
                "\"consign_time\": \"2018-01-01 00:00:00\"," +
                "\"update_time\": \"2018-01-01 00:00:00\"," +
                "\"offline_id\": 0," +
                "\"created\": \"2018-01-01 00:00:00\"," +
                "\"pay_time\": \"2018-01-01 00:00:00\"," +
                "\"confirm_time\": \"2018-01-01 00:00:00\"," +
                "\"is_retail_order\": false," +
                "\"success_time\": \"2018-01-01 00:00:00\"," +
                "\"order_extra\": {" +
                " \"is_from_cart\": \"false\"," +
                " \"cashier_id\": \"123\"," +
                " \"cashier_name\": \"收银员\"," +
                " \"invoice_title\": \"抬头\"," +
                " \"settle_time\": \"1525844042082\"," +
                " \"is_parent_order\": \"false\"," +
                " \"is_sub_order\": \"false\"," +
                " \"fx_order_no\": \"E2018\"," +
                " \"fx_kdt_id\": \"123\"," +
                " \"parent_order_no\": \"E2018\"," +
                " \"purchase_order_no\": \"E2018\"," +
                " \"dept_id\": \"123\"," +
                " \"create_device_id\": \"123\"," +
                " \"is_points_order\": \"false\"," +
                " \"id_card_number\": \"123\"" +
                "}," +
                "\"order_tags\": {" +
                " \"is_virtual\": false," +
                " \"is_purchase_order\": false," +
                " \"is_fenxiao_order\": false," +
                " \"is_member\": false," +
                " \"is_preorder\": false," +
                " \"is_offline_order\": false," +
                " \"is_multi_store\": false," +
                " \"is_settle\": null," +
                " \"is_payed\": false," +
                " \"is_secured_transactions\": false," +
                " \"is_postage_free\": false," +
                " \"is_feedback\": false," +
                " \"is_refund\": false" +
                "}" +
                "}," +
                "\"remark_info\": {" +
                "\"star\": 0," +
                "\"trade_memo\": \"尽快支付\"," +
                "\"buyer_message\": \"尽快发货\"" +
                "}," +
                "\"address_info\": {" +
                "\"receiver_name\": \"刘德华\"," +
                "\"delivery_address\": \"学院路8888号\"," +
                "\"address_extra\": \"{ln:23.43232,lat:9879.3443243}\"," +
                "\"delivery_district\": \"西湖区\"," +
                "\"delivery_end_time\": \"2018-01-01 00:00:00\"," +
                "\"delivery_postal_code\": \"000000\"," +
                "\"self_fetch_info\": \"{}\"," +
                "\"delivery_province\": \"浙江\"," +
                "\"delivery_start_time\": \"2018-01-01 00:00:00\"," +
                "\"receiver_tel\": \"15899898989\"," +
                "\"delivery_city\": \"杭州市\"" +
                "}," +
                "\"buyer_info\": {" +
                "\"buyer_id\": 63889," +
                "\"fans_id\": 3233," +
                "\"fans_type\": 1," +
                "\"fans_nickname\": \"ketty\"," +
                "\"buyer_phone\": \"15898999998\"," +
                "\"outer_user_id\": \"123\"" +
                "}," +
                "\"source_info\": {" +
                "\"source\": {" +
                " \"platform\": \"wx\"," +
                " \"wx_entrance\": \"wx_gzh\"" +
                "}," +
                "\"is_offline_order\": false" +
                "}," +
                "\"pay_info\": {" +
                "\"payment\": \"110.99\"," +
                "\"post_fee\": \"10.00\"," +
                "\"outer_transactions\": [" +
                " \"[]\"" +
                "]," +
                "\"total_fee\": \"100.99\"," +
                "\"transaction\": [" +
                " \"[]\"" +
                "]" +
                "}," +
                "\"child_info\": {" +
                "\"gift_no\": \"送礼编号\"," +
                "\"gift_sign\": \"送礼标记\"" +
                "}" +
                "  }," +
                "  \"delivery_order\": [" +
                "{" +
                "\"pk_id\": 1," +
                "\"express_state\": 1," +
                "\"express_type\": 1," +
                "\"oids\": [" +
                " {" +
                "   \"oid\": \"123\"" +
                " }" +
                "]" +
                "}" +
                "  ]," +
                "  \"order_promotion\": {" +
                "\"item\": {" +
                "\"promotions\": {" +
                " \"promotion_title\": \"满减送活动\"," +
                " \"promotion_type_name\": \"满减送\"," +
                " \"promotion_type_id\": 1," +
                " \"decrease\": \"10.00\"," +
                " \"promotion_type\": \"0\"" +
                "}," +
                "\"item_id\": 123," +
                "\"is_present\": false," +
                "\"sku_id\": 123456," +
                "\"oid\": 123456" +
                "}," +
                "\"order\": {" +
                "\"promotion_type\": \"0\"," +
                "\"promotion_title\": \"满减送活动\"," +
                "\"promotion_type_name\": \"满减送\"," +
                "\"promotion_type_id\": 123," +
                "\"discount_fee\": \"10.00\"," +
                "\"promotion_condition\": \"满减送XX\"," +
                "\"promotionContent\": \"满减送XX\"," +
                "\"promotion_id\": 32382137123," +
                "\"sub_promotion_type\": \"card\"," +
                "\"coupon_id\": \"3321hu2tv3123\"" +
                "}," +
                "\"adjust_fee\": \"1.00\"," +
                "\"item_discount_fee\": \"10.00\"," +
                "\"order_discount_fee\": \"1.00\"" +
                "  }," +
                "  \"refund_order\": [" +
                "{" +
                "\"refund_type\": 123," +
                "\"refund_fee\": \"0\"," +
                "\"refund_id\": \"0\"," +
                "\"refund_state\": 2," +
                "\"oids\": [" +
                " \"12\"" +
                "]" +
                "}" +
                "  ]" +
                "}");
        System.out.println(objectMapper.writeValueAsString(msgPushEntity));
    }
}

