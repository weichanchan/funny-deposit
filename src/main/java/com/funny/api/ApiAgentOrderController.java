package com.funny.api;

import com.funny.admin.agent.entity.AgentInfoEntity;
import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.AgentInfoService;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.utils.R;
import com.funny.utils.SignUtils;
import com.funny.utils.annotation.IgnoreAuth;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiAgentOrderController {
    //代理商id
    //private static final String agentId = "22501";
    //业务编号
    private static final String bussType = "13758";

    @Autowired
    private AgentOrderService agentOrderService;
    @Autowired
    private WareInfoService wareInfoService;
    @Autowired
    private AgentInfoService agentInfoService;

    /**
     * 提交充值&提取卡密
     * @return
     */
    @IgnoreAuth
    @RequestMapping("/beginDistill")
    public R beginDistill(@RequestParam Map<String, Object> params){
        //充值请求结果
        String isSuccess = "T" ;
        String errorCode = "";
        String sign = (String) params.get("sign");
        String signType = (String) params.get("signType");
        String timestamp = null;
        String version = (String) params.get("version");
        String agentOrderNo = null;
        Long agentPrice = null;
        Map map = new HashMap();

        //响应时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        timestamp = sdf.format(new Date());

        String jdOrderNo = (String) params.get("jdOrderNo");

        //校验签名
        boolean b = SignUtils.checkSign(params);
        if(b==false){
            isSuccess = "F";
            // 签名验证不正确（可退款）
            errorCode = "JDI_00002";
            map = getReturnMap(isSuccess, errorCode, agentOrderNo, jdOrderNo, agentPrice,  sign, signType, timestamp, version);
            return R.ok(map);
        }

        //根据京东订单号防重，如果已存在，返回订单信息
        AgentOrderEntity agentOrderEntity =  agentOrderService.queryObjectByJdOrderNo(jdOrderNo);
        if(agentOrderEntity !=  null){
            agentOrderNo = agentOrderEntity.getAgentOrderNo();
            map = getReturnMap(isSuccess, errorCode, agentOrderNo, jdOrderNo, agentPrice,  sign, signType, timestamp, version);
            return R.ok(map);
        }

        //对应商品
        String wareNo = (String) params.get("wareNo");
        WareInfoEntity wareInfoEntity = wareInfoService.queryObjectByWareNo(wareNo);
        if(wareInfoEntity==null){
            //没有对应商品（可退款）
            errorCode = "JDI_00003";
            isSuccess = "F";
            map = getReturnMap(isSuccess, errorCode, agentOrderNo, jdOrderNo, agentPrice,  sign, signType, timestamp, version);
            return R.ok(map);
        }

        //商品状态
        if(wareInfoEntity.getStatus()==2){
            //此商品不可售（可退款）
            errorCode = "JDI_00004";
            isSuccess = "F";
            map = getReturnMap(isSuccess, errorCode, agentOrderNo, jdOrderNo, agentPrice,  sign, signType, timestamp, version);
            return R.ok(map);
        }

        //商品类型
        Integer quantity = Integer.valueOf((String) params.get("quantity"));
        //直充类型商品只能买一个
        if(wareInfoEntity.getType()==1 && quantity>1){
            errorCode = "JDI_00001";
            isSuccess = "F";
            map = getReturnMap(isSuccess, errorCode, agentOrderNo, jdOrderNo, agentPrice,  sign, signType, timestamp, version);
            return R.ok(map);
        }

        //判断商品价格和成本价格是否相等
        agentPrice = wareInfoEntity.getAgentPrice();
        Long costPrice = Long.valueOf((String) params.get("costPrice"));
        if(!agentPrice.equals(costPrice)){
            // 成本价不正确（可退款）
            errorCode = "JDI_00005";
            isSuccess = "F";
            map = getReturnMap(isSuccess, errorCode, agentOrderNo, jdOrderNo, agentPrice,  sign, signType, timestamp, version);
            return R.ok(map);
        }

        Map<String, Object> param = new HashMap<String, Object>();
        for(Iterator it = params.keySet().iterator() ; it.hasNext();){
            String key = it.next().toString();
            String key1 = (String)params.get(key);
            //去掉协议参数
            if("sign".equals(key1) || "signType".equals(key1) || "timestamp".equals(key1) || "version".equals(key1)){
                param.put(key, params.get(key));
            }
        }

        agentOrderEntity.setJdOrderNo((String)params.get("jdOrderNo"));
        agentOrderEntity.setType(Integer.valueOf((String) params.get("type")));
        agentOrderEntity.setFinTime((String) params.get("finTime"));
        agentOrderEntity.setNotifyUrl((String) params.get("notifyUrl"));
        agentOrderEntity.setRechargeNum((String) params.get("rechargeNum"));
        agentOrderEntity.setQuantity(quantity);
        agentOrderEntity.setWareNo(wareNo);
        agentOrderEntity.setCostPrice(costPrice);
        agentOrderEntity.setFeatures((String) params.get("features"));
        agentOrderEntity.setCreateTime(new Date());
        agentOrderService.save(agentOrderEntity);

        return R.ok();
    }

    //获取返回参数map
    private Map getReturnMap( String isSuccess, String errorCode, String agentOrderNo,
                               String jdOrderNo, Long agentPrice, String sign,
                              String signType, String timestamp, String version) {
        Map map = new HashMap();
        map.put("isSuccess", isSuccess);
        map.put("errorCode", errorCode);
        if(!StringUtils.isEmpty(agentOrderNo)){
            map.put("agentOrderNo", agentOrderNo);
        }
        if(agentPrice != null){
            map.put("agentPrice", agentPrice);
        }
        map.put("jdOrderNo", jdOrderNo);
        map.put("sign", sign);
        map.put("signType", signType);
        map.put("timestamp", timestamp);
        map.put("version", version);
        return map;
    }

    /**
     * 订单查询
     */
    @IgnoreAuth
    @RequestMapping("/findDistill")
    public R findDistill(@RequestParam Map<String, Object> params){
        //订单查询结果
        String isSuccess = "T" ;
        String errorCode = "";
        Map<String, Object> map = new HashMap(15);

        //校验签名
        boolean b = SignUtils.checkSign(params);
        if(b==false){
            isSuccess = "F";
            // 签名验证不正确
            errorCode = "JDI_00002";
        }

        String jdOrderNo = (String) params.get("jdOrderNo");
        AgentOrderEntity agentOrder = agentOrderService.queryObjectByJdOrderNo(jdOrderNo);
        if(agentOrder!=null){
            map.put("jdOrderNo", jdOrderNo);
            map.put("agentOrderNo", agentOrder.getAgentOrderNo());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = sdf.format(new Date());
            map.put("time", time);
            map.put("status", agentOrder.getStatus());
            map.put("quantity", agentOrder.getQuantity());
            // TODO: 2018/9/5  卡密信息
        } else {
            isSuccess = "F";
            // 没有对应订单
            errorCode = "JDI_00007";
        }
        map.put("isSuccess", isSuccess);
        map.put("errorCode", errorCode);
        map.put("sign",  params.get("sign"));
        map.put("signType", params.get("signType"));
        map.put("timestamp", params.get("timestamp"));
        map.put("version", params.get("version"));
        return R.ok(map);
    }

    /**
     * 回调通知
     * @return
     */
    @IgnoreAuth
    @RequestMapping("/kamiNotify")
    public R kamiNotify(@RequestParam Map<String, Object> params){
        //回调结果
        String isSuccess = "T" ;
        String errorCode = "";
        Map<String, Object> map = new HashMap(15);

        //校验签名
        boolean b = SignUtils.checkSign(params);
        if(b==false){
            isSuccess = "F";
            // 签名验证不正确
            errorCode = "JDI_00002";
        }

        String agentId = (String) params.get("agentId");
        AgentInfoEntity agentInfoEntity = agentInfoService.queryObjectByAgentId(agentId);
        //代理商id是否存在
        if(agentInfoEntity!=null){
            // TODO: 2018/9/5  代理商id不正确
            String agentOrderNo = (String) params.get("agentOrderNo");
            //根据代理商订单号查询订单
            AgentOrderEntity agentOrder = agentOrderService.queryObjectByAgentOrderNo(agentOrderNo);
            //代理商订单是否存在
            if(agentOrder!=null){
                WareInfoEntity wareInfoEntity = wareInfoService.queryObjectByWareNo(agentOrder.getWareNo());
                String agentId1 = wareInfoEntity.getAgentId();
                //代理商id是否正确
                if(agentId1.equals(agentId)){
                    String jdOrderNo = (String) params.get("jdOrderNo");Integer status = agentOrder.getStatus();
                    //比较 订单状态
                    if(agentOrder.getStatus().equals(status)){
                        // TODO: 2018/9/5  卡密信息
                    } else {
                        isSuccess = "F";
                        //对应状态不存在
                        errorCode = "JDO_10005";
                    }
                } else {
                    isSuccess = "F";
                    //代理商ID不正确
                    errorCode = "JDO_10007";
                }

            } else {
                isSuccess = "F";
                // 此订单不存在
                errorCode = "JDO_10006";
            }

        } else {
            isSuccess = "F";
            //商家不存在
            errorCode = "JDO_00001";
        }
        map.put("isSuccess", isSuccess);
        map.put("errorCode", errorCode);
        return R.ok(map);
    }

}
