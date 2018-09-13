package com.funny.utils;

import org.apache.log4j.Logger;

import java.util.*;


/**
 * 配置文件config.properties所有配置
 *
 * @author Administrator
 */
public class PropertiesContent {
    public static Logger log = Logger.getLogger(PropertiesContent.class);
    public static Map<String, Object> config = new HashMap<String, Object>();
    public static Properties properties=new Properties();
    public static PropertiesContent me=new PropertiesContent();
    static {
        //读取config.properties文件，不加.properties后缀，不加路径名
        ResourceBundle rb = ResourceBundle.getBundle("config");
        Enumeration<String> cfgs = rb.getKeys();
        while (cfgs.hasMoreElements()) {
            String key = cfgs.nextElement();
            String val=rb.getString(key);
            if (key.contains("mail.password")==true) {
                try {
//                    config.put(key, CipherUtil.decryptData(val));
                } catch (Exception e) {
                    log.error("对参数解密异常",e);
                }
            }else{
                config.put(key,val);
            }
        }
        properties.putAll(config);
    }
    public static String get(String key){
        return (String)config.get(key);
    }
    public static String get(String key, String def) {
        String v=get(key);
        if(v==null){
            return def;
        }else{
            return v;
        }
    }
    public static Object getObj(String key){
        return config.get(key);
    }
    public static Boolean getToBool(String key,Boolean def){
        try{
            return Boolean.valueOf((String)config.get(key));
        }catch(Exception e){
            return def;
        }
    }
    public static Integer getToInteger(String key,Integer def){
        try{
            return Integer.valueOf((String)config.get(key));
        }catch(Exception e){
            return def;
        }
    }
    public static Long getToLong(String key,Long def){
        try{
            return Long.valueOf((String)config.get(key));
        }catch(Exception e){
            return def;
        }
    }
}
