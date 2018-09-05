package com.funny.api;

import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.utils.R;
import com.funny.utils.SignUtils;
import com.funny.utils.annotation.IgnoreAuth;
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
    private static final String agentId = "22501";
    //业务编号
    private static final String bussType = "13758";

    @Autowired
    private AgentOrderService agentOrderService;
    @Autowired
    private WareInfoService wareInfoService;

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
        String agentOrderNo;
        Map<String, Object> map = new HashMap(15);

        //校验签名
        boolean b = SignUtils.checkSign(params);
        if(b==false){
            isSuccess = "F";
            // 签名验证不正确（可退款）
            errorCode = "JDI_00002";
        }

        // TODO: 2018/9/5  修改状态时加锁
        String jdOrderNo = (String) params.get("jdOrderNo");
        AgentOrderEntity agentOrderEntity =  agentOrderService.queryObjectByJdOrderNo(jdOrderNo);

        //根据订单号防重，如果不存在，则创建新订单
        if(agentOrderEntity==null ){
            String wareNo = (String) params.get("wareNo");
            WareInfoEntity wareInfoEntity = wareInfoService.queryObjectByWareNo(wareNo);
            if(wareInfoEntity==null){
                //没有对应商品（可退款）
                errorCode = "JDI_00003";
                isSuccess = "F";
            } else if(wareInfoEntity.getStatus()==2){
                //此商品不可售（可退款）
                errorCode = "JDI_00004";
                isSuccess = "F";
            } else {
                Integer quantity = Integer.valueOf((String) params.get("quantity"));
                //直充类型商品只能买一个
                if(wareInfoEntity.getType()==1 && quantity>1){
                    errorCode = "JDI_00001";
                    isSuccess = "F";
                } else {
                    Long costPrice = Long.valueOf((String) params.get("costPrice"));
                    AgentOrderEntity agentOrder = new AgentOrderEntity();
                    agentOrder.setJdOrderNo((String)params.get("jdOrderNo"));
                    agentOrder.setType(Integer.valueOf((String) params.get("type")));
                    agentOrder.setFinTime((String) params.get("finTime"));
                    agentOrder.setNotifyUrl((String) params.get("notifyUrl"));
                    agentOrder.setRechargeNum((String) params.get("rechargeNum"));
                    agentOrder.setQuantity(quantity);
                    agentOrder.setWareNo(wareNo);
                    agentOrder.setCostPrice(costPrice);
                    agentOrder.setFeatures((String) params.get("features"));
                    //生成代理商订单号
                    agentOrderNo = createAgentOrderNo();
                    agentOrder.setAgentOrderNo(agentOrderNo);
                    agentOrder.setCreateTime(new Date());
                    Long agentPrice = wareInfoEntity.getAgentPrice();
                    //判断商品价格和成本价格是否相等
                    if(agentPrice!=costPrice){
                        //充值失败
                        agentOrder.setStatus(2);
                        isSuccess = "F";
                        // 成本价不正确（可退款）
                        errorCode = "JDI_00005";
                        isSuccess = "F";
                        map.put("agentPrice", agentPrice);
                    }
                    agentOrderService.save(agentOrder);
                    map.put("agentOrderNo", agentOrderNo);
                }
            }
        } else {
            map.put("agentOrderNo", agentOrderEntity.getAgentOrderNo());
        }

        map.put("isSuccess", isSuccess);
        map.put("errorCode", errorCode);
        map.put("jdOrderNo", jdOrderNo);
        map.put("sign",  params.get("sign"));
        map.put("signType", params.get("signType"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        map.put("timestamp", timestamp);
        map.put("version", params.get("version"));

        return R.ok(map);
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
        String agentId1 = (String) params.get("agentId");
        //代理商id是否存在
        if(agentId.equals(agentId1)){
            String jdOrderNo = (String) params.get("jdOrderNo");
            String agentOrderNo = (String) params.get("agentOrderNo");
            //根据代理商订单号查询订单
            AgentOrderEntity agentOrder = agentOrderService.queryObjectByAgentOrderNo(agentOrderNo);
            if(agentOrder!=null){
                Integer status = agentOrder.getStatus();
                //比较 "传入的状态"与"订单状态"，如果相同
                if(agentOrder.getStatus().equals(status)){
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
                    //对应状态不存在
                    errorCode = "JDO_10005";
                }

            } else {
                isSuccess = "F";
                // 次订单不存在
                errorCode = "JDO_10006";
            }
        } else {
            isSuccess = "F";
            //商家不存在
            errorCode = "JDO_00001";
        }

        map.put("isSuccess", isSuccess);
        map.put("errorCode", errorCode);
        map.put("sign",  params.get("sign"));
        map.put("signType", params.get("signType"));
        map.put("timestamp", params.get("timestamp"));
        map.put("version", params.get("version"));
        return R.ok(map);
    }

    //流水号格式
    private static String SERIAL_NUMBER="00000";
    /**
     * 生成代理商订单号
     * @return
     */
    public String createAgentOrderNo() {
        Long maxId = agentOrderService.getMaxId();
        if(maxId==null){
            maxId = 0l;
        }
        String num = String.valueOf(maxId);
        String agentOrderNo = null;
        String prefix = "SP";
        int count = SERIAL_NUMBER.length();
        StringBuilder sb = new StringBuilder();
        agentOrderNo = prefix + SERIAL_NUMBER.substring(0, count-num.length()) + num;
        return agentOrderNo;
    }

}
