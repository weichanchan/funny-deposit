package com.funny.api;

import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.utils.R;
import com.funny.utils.SignUtils;
import com.funny.utils.annotation.IgnoreAuth;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiAgentOrderController {
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

        // TODO: 2018/9/4 根据订单号防重
        // TODO: 2018/9/5  修改状态时加锁 
        String jdOrderNo = (String) params.get("jdOrderNo");
        AgentOrderEntity agentOrderEntity =  agentOrderService.queryObjectByJdOrderNo(jdOrderNo);
        //如果不存在，则创建新订单
        if(agentOrderEntity==null ){
            String wareNo = (String) params.get("wareNo");
            WareInfoEntity wareInfoEntity = wareInfoService.queryObjectByWareNo(wareNo);
            if(wareInfoEntity==null){
                //没有对应商品（可退款）
                errorCode = "JDI_00003";
            }
            if(wareInfoEntity.getStatus()==2){
                //此商品不可售（可退款）
                errorCode = "JDI_00004";
            }
            Long costPrice = Long.valueOf((String) params.get("costPrice"));
            AgentOrderEntity agentOrder = new AgentOrderEntity();
            agentOrder.setJdOrderNo((String)params.get("jdOrderNo"));
            agentOrder.setType(Integer.valueOf((String) params.get("type")));
            agentOrder.setFintime((String) params.get("finTime"));
            agentOrder.setNotifyurl((String) params.get("notifyUrl"));
            agentOrder.setRechargeNum((String) params.get("rechargeNum"));
            agentOrder.setQuantity(Integer.valueOf((String) params.get("quantity")));
            agentOrder.setWareno(wareNo);
            agentOrder.setCostprice(costPrice);
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
                map.put("agentPrice", agentPrice);
            }
            agentOrderService.save(agentOrder);
        }

        map.put("isSuccess", isSuccess);
        map.put("errorCode", errorCode);
        map.put("jdOrderNo", jdOrderNo);
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
