package com.funny.admin.agent.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.funny.admin.agent.entity.WareFuluRoleEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.WareFuluRoleService;
import com.funny.admin.system.service.SysRoleService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.utils.PageUtils;
import com.funny.utils.Query;
import com.funny.utils.R;


/**
 * 商品信息表
 *
 * @author weicc
 * @email
 * @date 2019-02-27 09:05:27
 */
@RestController
@RequestMapping("warefuluinfo")
public class WareFuluInfoController {
    @Autowired
    private WareFuluInfoService wareFuluInfoService;
    @Autowired
    private WareFuluRoleService wareFuluRoleService;
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("warefuluinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        // 筛选出拼多多的商品
        if ("1".equals(query.get("type"))) {
            query.put("type", "拼多多");
        }
        // 筛选除了拼多多以外的商品
        if ("0".equals(query.get("type"))) {
            query.remove("type");
            query.put("other", "拼多多");
        }
        List<WareFuluInfoEntity> wareFuluInfoList = wareFuluInfoService.queryList(query);
        int total = wareFuluInfoService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(wareFuluInfoList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("warefuluinfo:info")
    public R info(@PathVariable("id") Long id) {
        WareFuluInfoEntity wareFuluInfo = wareFuluInfoService.queryObject(id);
        List<Long> roleIdList = wareFuluRoleService.queryRoleList(wareFuluInfo.getId());
        wareFuluInfo.setRoleList(roleIdList);
        return R.ok().put("wareFuluInfo", wareFuluInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("warefuluinfo:save")
    public R save(@RequestBody WareFuluInfoEntity wareFuluInfo) {
        wareFuluInfo.setCreateTime(new Date());

        wareFuluInfoService.save(wareFuluInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("warefuluinfo:update")
    public R update(@RequestBody WareFuluInfoEntity wareFuluInfo) {
        wareFuluInfoService.update(wareFuluInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("warefuluinfo:delete")
    public R delete(@RequestBody Long[] ids) {
        wareFuluInfoService.deleteBatch(ids);

        return R.ok();
    }

//    /**
//     * 删除
//     */
//    @RequestMapping("/refresh")
//    public R refresh() {
//        List<WareFuluInfoEntity> wareFuluInfoEntities = wareFuluInfoService.queryList(null);
//        String roleName = "";
//        for (WareFuluInfoEntity wareFuluInfoEntity : wareFuluInfoEntities) {
//            List<Long> roleIdList = wareFuluRoleService.queryRoleList(wareFuluInfoEntity.getId());
//            wareFuluInfoEntity.setRoleList(roleIdList);
//            wareFuluInfoService.update(wareFuluInfoEntity);
//        }
//
//        return R.ok();
//    }

}
