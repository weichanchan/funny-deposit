package com.funny.admin.agent.controller;

import java.util.List;
import java.util.Map;

import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.agent.service.WareInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.funny.utils.PageUtils;
import com.funny.utils.Query;
import com.funny.utils.R;


/**
 * 代理商订单表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-03 10:23:02
 */
@RestController
@RequestMapping("agentorder")
public class AgentOrderController {
    @Autowired
    private AgentOrderService agentOrderService;
    @Autowired
    private WareInfoService wareInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("agentorder:list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);

        List<AgentOrderEntity> agentOrderList = agentOrderService.queryList(query);
        int total = agentOrderService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(agentOrderList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("agentorder:info")
    public R info(@PathVariable("id") Long id) {
        AgentOrderEntity agentOrder = agentOrderService.queryObject(id);

        return R.ok().put("agentOrder", agentOrder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("agentorder:save")
    public R save(@RequestBody AgentOrderEntity agentOrder) {
        //生成代理商订单号
        String agentOrderNo = createAgentOrderNo();
        agentOrder.setAgentOrderNo(agentOrderNo);
        agentOrderService.save(agentOrder);

        return R.ok();
    }

    /**
     * 处理订单
     *
     * @param agentOrder
     */
    public void handleAgentOrder(AgentOrderEntity agentOrder) {
        String wareNo = agentOrder.getWareNo();
        WareInfoEntity wareInfo = wareInfoService.queryObjectByWareNo(wareNo);
        Integer wareType = wareInfo.getType();
        //商品是卡密类型
        if (wareType == 2) {
            // TODO: 2018/9/6  获取卡密类型
        }
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("agentorder:update")
    public R update(@RequestBody AgentOrderEntity agentOrder) {
        agentOrderService.update(agentOrder);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("agentorder:delete")
    public R delete(@RequestBody Long[] ids) {
        agentOrderService.deleteBatch(ids);

        return R.ok();
    }

    //流水号格式
    private static String SERIAL_NUMBER = "00000";

    /**
     * 生成代理商订单号
     *
     * @return
     */
    public String createAgentOrderNo() {
        Long maxId = agentOrderService.getMaxId();
        if (maxId == null) {
            maxId = 0l;
        }
        String num = String.valueOf(maxId);
        String agentOrderNo = null;
        String prefix = "SP";
        int count = SERIAL_NUMBER.length();
        StringBuilder sb = new StringBuilder();
        agentOrderNo = prefix + SERIAL_NUMBER.substring(0, count - num.length()) + num;
        return agentOrderNo;
    }
}
