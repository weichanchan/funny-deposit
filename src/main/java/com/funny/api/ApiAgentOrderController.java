package com.funny.api;

import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.CardInfoEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.entity.WareInfoVO;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.agent.service.CardInfoService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.api.event.notify.AgentOrderListener;
import com.funny.api.event.notify.AgentOrderNotifyEvent;
import com.funny.utils.ConfigUtils;
import com.funny.utils.EncryptUtil;
import com.funny.utils.SignUtils;
import com.funny.utils.annotation.IgnoreAuth;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiAgentOrderController {
    Logger logger = LoggerFactory.getLogger(AgentOrderListener.class);

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    private AgentOrderService agentOrderService;
    @Autowired
    private WareInfoService wareInfoService;
    @Autowired
    private CardInfoService cardInfoService;
    @Autowired
    private ConfigUtils configUtils;

    /**
     * 提交充值&提取卡密
     *
     * @return
     */
    @IgnoreAuth
    @RequestMapping("/beginDistill")
    public Map beginDistill(String sign, String signType, String timestamp, String version, String jdOrderNo,
                            Integer type, String finTime, String notifyUrl, String rechargeNum, Integer quantity, String wareNo,
                            Long costPrice, String features) {
        //充值请求结果
        String isSuccess = "T";
        String errorCode = "";

        Long agentPrice = null;
        Map map;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("sign", sign);
            params.put("signType", signType);
            params.put("timestamp", timestamp);
            params.put("version", version);
            params.put("jdOrderNo", jdOrderNo);
            params.put("type", type.toString());
            params.put("finTime", finTime);
            params.put("notifyUrl", notifyUrl);
            params.put("rechargeNum", rechargeNum);
            params.put("quantity", quantity.toString());
            params.put("wareNo", wareNo);
            params.put("costPrice", costPrice.toString());
            params.put("features", features);
            //校验签名
            boolean b = SignUtils.checkSign(params, configUtils.getSecretKey(), configUtils.getVersionNo());
            if (b == false) {
                isSuccess = "F";
                // 签名验证不正确（可退款）
                errorCode = "JDI_00002";
                map = getReturnMap(isSuccess, errorCode, null, jdOrderNo, null, sign, signType, timestamp, version);
                return map;
            }
            String agentOrderNo = UUID.randomUUID().toString().replace("-", "");
            //根据京东订单号防重，如果已存在，返回订单信息
            AgentOrderEntity agentOrderEntity = agentOrderService.queryObjectByJdOrderNo(jdOrderNo);
            if (agentOrderEntity != null) {
                agentOrderNo = agentOrderEntity.getAgentOrderNo();
                return getReturnMap(isSuccess, errorCode, agentOrderNo, jdOrderNo, null, sign, signType, timestamp, version);
            }

            agentOrderEntity = new AgentOrderEntity();
            agentOrderEntity.setAgentOrderNo(agentOrderNo);
            //对应商品
            WareInfoEntity wareInfoEntity = wareInfoService.queryObjectByWareNo(wareNo);
            if (wareInfoEntity == null) {
                //没有对应商品（可退款）
                return getReturnMap("F", "JDI_00003", null, jdOrderNo, null, sign, signType, timestamp, version);
            }

            //商品状态
            if (wareInfoEntity.getStatus() == 2) {
                //此商品不可售（可退款）
                return getReturnMap("F", "JDI_00004", null, jdOrderNo, null, sign, signType, timestamp, version);
            }

            //直充类型商品只能买一个
            Integer rechargeType = wareInfoEntity.getType();
            if (quantity <= 0 || (rechargeType == 1 && quantity > 1)) {
                return getReturnMap("F", "JDI_00001", null, jdOrderNo, null, sign, signType, timestamp, version);
            }
            //直充类型商品充值号码不为空
            if (rechargeType == 1 && StringUtils.isEmpty(rechargeNum)) {
                return getReturnMap("F", "JDI_00001", null, jdOrderNo, null, sign, signType, timestamp, version);
            }
            //判断商品价格和成本价格是否相等
            agentPrice = wareInfoEntity.getAgentPrice();
            if (!agentPrice.equals(costPrice)) {
                // 成本价不正确（可退款）
                return getReturnMap("F", "JDI_00005", null, jdOrderNo, agentPrice, sign, signType, timestamp, version);
            }

            if (features != null) {
                try {
                    //特殊属性解密
                    features = EncryptUtil.decryptBase64(URLDecoder.decode(features, "UTF-8"), configUtils.getSecretKey());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            //直充类型，且参数合法
            if (rechargeType == 1) {
                saveAgentOrder(sign, signType, timestamp, version, jdOrderNo, agentOrderEntity, wareNo, quantity, rechargeNum, costPrice, type, finTime, notifyUrl, features);
                return getReturnMap("T", "", agentOrderNo, jdOrderNo, null, sign, signType, timestamp, version);
            }

            //卡密类型
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("wareNo", wareNo);
            queryMap.put("num", quantity);
            queryMap.put("status", 1);
            List<CardInfoEntity> cardInfoLists = cardInfoService.queryListNum(queryMap);
            if (cardInfoLists == null || cardInfoLists.size() < quantity) {
                logger.error("库存不足");
                return getReturnMap("F", "JDI_00004", null, jdOrderNo, null, sign, signType, timestamp, version);
            }

            saveAgentOrder(sign, signType, timestamp, version, jdOrderNo, agentOrderEntity, wareNo, quantity, rechargeNum, costPrice, type, finTime, notifyUrl, features);
            agentOrderEntity = agentOrderService.queryObjectByJdOrderNo(jdOrderNo);

            String cardInfoString = "";
            //卡密类型充值，获取卡密串
            for (int i = 0; i < cardInfoLists.size(); i++) {
                CardInfoEntity cardInfo = cardInfoLists.get(i);
                //将卡密状态改为2：已使用，同时存入代理商订单编号
                cardInfo.setRechargeTime(new Date());
                cardInfo.setStatus(2);
                cardInfo.setAgentOrderNo(agentOrderEntity.getAgentOrderNo());
                cardInfoService.update(cardInfo);
                if (i == 0) {
                    cardInfoString += getCardInfoString(cardInfo);
                } else {
                    cardInfoString += "|" + getCardInfoString(cardInfo);
                }
            }

            //根据商品编号查询商品库存
            WareInfoVO wareInfoVO = wareInfoService.queryObjectAvailable(wareNo);
            //库存为0，设置商品不可售
            if(wareInfoVO.getAvailable()==0){
                wareInfoEntity.setStatus(2);
                wareInfoService.update(wareInfoEntity);
            }
            //设置卡密串
            agentOrderEntity.setCardInfo(cardInfoString);
            agentOrderEntity.setStatus(3);
            agentOrderEntity.setRechargeStatus(1);
            agentOrderEntity.setHandleTime(new Date());
            agentOrderService.update(agentOrderEntity);

            logger.info("下单成功！");
            applicationContext.publishEvent(new AgentOrderNotifyEvent(agentOrderEntity.getId(), agentOrderEntity, "", wareInfoEntity, ""));

            return getReturnMap("T", "", agentOrderNo, jdOrderNo, null, sign, signType, timestamp, version);
        } catch (Exception e) {
            logger.error("下单错误", e);
            return getReturnMap("F", "JDI_00010", null, null, null, sign, signType, timestamp, version);
        }

    }

    /**
     * 获得指定卡密信息的卡密串
     *
     * @param cardInfo
     * @return
     */
    private String getCardInfoString(CardInfoEntity cardInfo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String accountNo = cardInfo.getAccountNo();
        if (!StringUtils.isEmpty(accountNo)) {
            accountNo += "_";
        } else {
            accountNo = "无_";
        }
        String password = cardInfo.getPassword();
        if (!StringUtils.isEmpty(password)) {
            password += "_";
        } else {
            password = "无_";
        }
        String expiryDate = sdf.format(cardInfo.getExpiryDate());

        return accountNo + password + expiryDate;
    }

    private AgentOrderEntity saveAgentOrder(String sign, String signType, String timestamp, String version,
                                            String jdOrderNo, AgentOrderEntity agentOrderEntity, String wareNo, Integer quantity,
                                            String rechargeNum, Long costPrice, Integer type, String finTime,
                                            String notifyUrl, String features) {
        agentOrderEntity.setJdOrderNo(jdOrderNo);
        agentOrderEntity.setType(type);
        // TODO: 2018/9/13  清算时间暂时填当前时间
        agentOrderEntity.setFinTime(finTime);
        agentOrderEntity.setNotifyUrl(notifyUrl);
        agentOrderEntity.setRechargeNum(rechargeNum);
        agentOrderEntity.setQuantity(quantity);
        agentOrderEntity.setWareNo(wareNo);
        agentOrderEntity.setCostPrice(costPrice);
        agentOrderEntity.setFeatures(features);
        agentOrderEntity.setCreateTime(new Date());
        agentOrderEntity.setSign(sign);
        agentOrderEntity.setSignType(signType);
        agentOrderEntity.setTimestamp(timestamp);
        agentOrderEntity.setVersion(version);
        //默认订单状态：未充值
        agentOrderEntity.setRechargeStatus(0);
        //默认充值状态：新创建
        agentOrderEntity.setStatus(1);
        agentOrderService.save(agentOrderEntity);
        return agentOrderEntity;
    }

    /**
     * 获取返回参数map
     *
     * @param isSuccess
     * @param errorCode
     * @param agentOrderNo
     * @param jdOrderNo
     * @param agentPrice
     * @param sign
     * @param signType
     * @param timestamp
     * @param version
     * @return
     */
    private Map getReturnMap(String isSuccess, String errorCode, String agentOrderNo,
                             String jdOrderNo, Long agentPrice, String sign,
                             String signType, String timestamp, String version) {
        Map map = new HashMap();
        map.put("isSuccess", isSuccess);
        if (!StringUtils.isEmpty(errorCode)) {
            map.put("errorCode", errorCode);
        }
        if (!StringUtils.isEmpty(agentOrderNo)) {
            map.put("agentOrderNo", agentOrderNo);
        }
        if (agentPrice != null) {
            map.put("agentPrice", agentPrice);
        }

        map.put("jdOrderNo", jdOrderNo);
        map.put("timestamp", timestamp);
        map.put("version", version);
        map.remove("code");
        sign = SignUtils.getSign(map, configUtils.getSecretKey());
        map.put("sign", sign);
        map.put("signType", signType);
        map.put("errorCode", errorCode);
        return map;
    }

    /**
     * 订单查询
     *
     * @param params
     * @return
     */
    @IgnoreAuth
    @RequestMapping("/findDistill")
    public Map findDistill(@RequestParam Map<String, Object> params) {
        //查询请求结果
        String sign = (String) params.get("sign");
        String signType = (String) params.get("signType");
        String version = (String) params.get("version");
        String jdOrderNo = (String) params.get("jdOrderNo");
        String timestamp = "";
        //响应时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        timestamp = sdf.format(new Date());
        Map<String, Object> map = new HashMap(15);
        //校验签名
        boolean b = SignUtils.checkSign(params, configUtils.getSecretKey(), configUtils.getVersionNo());
        if (b == false) {
            // 签名验证不正确
            return getReturnMap("F", "JDI_00002", null, jdOrderNo, null, null, null, null, signType, timestamp, version);
        }


        AgentOrderEntity agentOrder = agentOrderService.queryObjectByJdOrderNo(jdOrderNo);
        if (agentOrder == null) {
            return getReturnMap("F", "JDI_00007", null, jdOrderNo, null, null, null, null, signType, timestamp, version);
        }

        String agentOrderNo = agentOrder.getAgentOrderNo();
        Integer rechargeStatus = agentOrder.getRechargeStatus();
        String time = sdf.format(new Date());
        Integer quantity = agentOrder.getQuantity();
        String cardInfo = null;
        String wareNo = agentOrder.getWareNo();
        WareInfoEntity wareInfoEntity = wareInfoService.queryObjectByWareNo(wareNo);
        Integer wareType = wareInfoEntity.getType();
        //如果是直充类型，且充值状态为未充值，则返回状态设置为充值中
        if (wareType == 1 && rechargeStatus == 0) {
            rechargeStatus = 3;
        }
        if (wareType != 1) {
            cardInfo = EncryptUtil.encryptBase64(agentOrder.getCardInfo(), configUtils.getSecretKey());
        }
        return getReturnMap("T", "", agentOrderNo, jdOrderNo, rechargeStatus, time, quantity, cardInfo, signType, sdf.format(new Date()), version);

    }

    /**
     * 获取订单查询返回值
     *
     * @param isSuccess
     * @param errorCode
     * @param agentOrderNo
     * @param jdOrderNo
     * @param status
     * @param time
     * @param quantity
     * @param cardInfo
     * @param signType
     * @param timestamp
     * @param version
     * @return
     */
    private Map getReturnMap(String isSuccess, String errorCode, String agentOrderNo,
                             String jdOrderNo, Integer status, String time,
                             Integer quantity, String cardInfo, String signType,
                             String timestamp, String version) {
        Map<String, Object> map = new HashMap<>();
        map.put("isSuccess", isSuccess);
        if (!StringUtils.isEmpty(errorCode)) {
            map.put("errorCode", errorCode);
        }
        if (!StringUtils.isEmpty(agentOrderNo)) {
            map.put("agentOrderNo", agentOrderNo);
        }
        if (!StringUtils.isEmpty(jdOrderNo)) {
            map.put("jdOrderNo", jdOrderNo);
        }
        //提取结果时间，状态为提取中时可不传
        // TODO: 2018/9/13  订单状态好像没有提取中这个状态？
        if (!StringUtils.isEmpty(time)) {
            map.put("time", time);
        }
        if (status != null) {
            map.put("status", status);
        }
        if (quantity != null) {
            map.put("quantity", quantity);
        }
        if (!StringUtils.isEmpty(cardInfo)) {
            map.put("cardInfo", cardInfo);
        }

        map.put("timestamp", timestamp);
        map.put("version", version);
        String sign = SignUtils.getSign(map, configUtils.getSecretKey());
        map.put("sign", sign);
        map.put("signType", signType);
        map.put("errorCode", errorCode);
        return map;
    }
}
