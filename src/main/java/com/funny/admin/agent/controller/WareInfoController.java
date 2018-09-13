package com.funny.admin.agent.controller;

import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.utils.PageUtils;
import com.funny.utils.PropertiesContent;
import com.funny.utils.Query;
import com.funny.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 商品信息表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-04 11:13:01
 */
@RestController
@RequestMapping("wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("wareinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);

        List<WareInfoEntity> wareInfoList = wareInfoService.queryList(query);
        int total = wareInfoService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(wareInfoList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("wareinfo:info")
    public R info(@PathVariable("id") Long id) {
        WareInfoEntity wareInfo = wareInfoService.queryObject(id);
        List<Long> roleIdList = wareInfoService.queryRoleIdList(wareInfo.getId());
        wareInfo.setRoleIdList(roleIdList);
        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("wareinfo:save")
    public R save(@RequestBody WareInfoEntity wareInfo) {
        // TODO: 2018/9/10  代理商id，目前只有一个，先写固定值
        wareInfo.setAgentId(PropertiesContent.get("agentId"));
        if (wareInfo.getRoleIdList().size() == 0) {
            return R.error("请选择处理该商品订单的客服角色。");
        }
        wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("wareinfo:update")
    public R update(@RequestBody WareInfoEntity wareInfo) {
        wareInfoService.update(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("wareinfo:delete")
    public R delete(@RequestBody Long[] ids) {
        wareInfoService.deleteBatch(ids);

        return R.ok();
    }

}
