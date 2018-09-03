package com.funny.api;

import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.utils.R;
import com.funny.utils.annotation.IgnoreAuth;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api")
public class ApiAgentOrderController {
    @Autowired
    private AgentOrderService orderService;

    /**
     * 提交充值&提取卡密
     * @return
     */
    @IgnoreAuth
    @RequestMapping("/beginDistill")
    @RequiresPermissions("agentorder:save")
    public R beginDistill(@RequestParam String sign, @RequestParam String signType,
                          @RequestParam String timestamp, @RequestParam String version, @RequestParam String jdOrderNo,
                          @RequestParam Integer type, @RequestParam String finTime, @RequestParam String notifyUrl,
                          @RequestParam String rechargeNum, @RequestParam Integer quantity, @RequestParam String wareNo,
                          @RequestParam Long costPrice, @RequestParam String features){
        // TODO: 2018/9/3  协议参数验证：检查版本、验密、时间戳是否是一个小时以内、京东账单是否已处理 
        AgentOrderEntity agentOrder = new AgentOrderEntity();
        agentOrder.setJdOrderId(jdOrderNo);
        agentOrder.setType(type);
        agentOrder.setFintime(finTime);
        agentOrder.setNotifyurl(notifyUrl);
        agentOrder.setRechargeNum(rechargeNum);
        agentOrder.setQuantity(quantity);
        agentOrder.setWareno(wareNo);
        agentOrder.setCostprice(costPrice);
        agentOrder.setFeatures(features);
        Long maxId = orderService.getMaxId();
        if(maxId==null){
            maxId = 0l;
        }
        String agentOrderNo = createAgentOrderNo(maxId+1);
        agentOrder.setAgentOrderNo(agentOrderNo);
        agentOrder.setCreateTime(new Date());
        orderService.save(agentOrder);
        return R.ok();
    }


    //流水号格式
    private static String SERIAL_NUMBER="00000";
    public String createAgentOrderNo(long maxId) {
        String num = String.valueOf(maxId);
        String agentOrderNo = null;
        String prefix = "SP";
        int count = SERIAL_NUMBER.length();
        StringBuilder sb = new StringBuilder();
        agentOrderNo = prefix + SERIAL_NUMBER.substring(0, count-num.length()) + num;
        return agentOrderNo;
    }
}
