package com.funny.admin.agent.controller;

import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.utils.PageUtils;
import com.funny.utils.Query;
import com.funny.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
        List<AgentOrderEntity> agentOrderEntityList = new ArrayList<AgentOrderEntity>();
        //将清算时间格式"yyyyMMddHHmmss"转化为"yyyy-MM-dd HH:mm:ss"
        String reg = "(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})";
        for (AgentOrderEntity agentOrderEntity : agentOrderList) {
            String fTime = agentOrderEntity.getFinTime();
            fTime = fTime.replaceAll(reg, "$1-$2-$3 $4:$5:$6");
            agentOrderEntity.setFinTime(fTime);
            agentOrderEntityList.add(agentOrderEntity);
        }
        int total = agentOrderService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(agentOrderEntityList, total, query.getLimit(), query.getPage());

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
     * 开始处理订单
     *
     * @param id
     */
    @RequestMapping("/handleAgentOrder/{id}")
    @RequiresPermissions("agentorder:update")
    public R handleAgentOrder(@PathVariable("id") Long id) {
        //  Long id = (Long) params.get("id");
        AgentOrderEntity agentOrderEntity = agentOrderService.queryObject(id);
        if (agentOrderEntity != null) {
            agentOrderEntity.setStatus(2);
            agentOrderEntity.setRechargeStatus(3);
            agentOrderService.update(agentOrderEntity);
        }
        return R.ok();
    }

    // TODO: 2018/9/7 处理成功、失败
    @RequestMapping("/handleSuccess/{id}")
    public R handleSuccess(@PathVariable("id") Long id) {
        agentOrderService.handleSuccess(id);
        return R.ok();
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
