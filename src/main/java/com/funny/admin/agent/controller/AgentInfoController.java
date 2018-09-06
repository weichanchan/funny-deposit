package com.funny.admin.agent.controller;

import java.util.List;
import java.util.Map;

import com.funny.admin.agent.entity.AgentInfoEntity;
import com.funny.admin.agent.service.AgentInfoService;
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
 * 代理商信息表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-05 18:04:57
 */
@RestController
@RequestMapping("agentinfo")
public class AgentInfoController {
    @Autowired
    private AgentInfoService agentInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("agentinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);

        List<AgentInfoEntity> agentInfoList = agentInfoService.queryList(query);
        int total = agentInfoService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(agentInfoList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("agentinfo:info")
    public R info(@PathVariable("id") Long id) {
        AgentInfoEntity agentInfo = agentInfoService.queryObject(id);

        return R.ok().put("agentInfo", agentInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("agentinfo:save")
    public R save(@RequestBody AgentInfoEntity agentInfo) {
        agentInfoService.save(agentInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("agentinfo:update")
    public R update(@RequestBody AgentInfoEntity agentInfo) {
        agentInfoService.update(agentInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("agentinfo:delete")
    public R delete(@RequestBody Long[] ids) {
        agentInfoService.deleteBatch(ids);

        return R.ok();
    }

}
