package com.funny.admin.agent.controller;

import java.util.List;
import java.util.Map;

import com.funny.admin.common.AbstractController;
import com.funny.utils.PageUtils;
import com.funny.utils.Query;
import com.funny.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.funny.admin.agent.entity.ThridPlatformGateEntity;
import com.funny.admin.agent.service.ThridPlatformGateService;


/**
 * 
 * 
 * @author weicc
 * @email 
 * @date 2019-07-10 16:39:52
 */
@RestController
@RequestMapping("thridplatformgate")
public class ThridPlatformGateController extends AbstractController {
	@Autowired
	private ThridPlatformGateService thridPlatformGateService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("thridplatformgate:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<ThridPlatformGateEntity> thridPlatformGateList = thridPlatformGateService.queryList(query);
		int total = thridPlatformGateService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(thridPlatformGateList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("thridplatformgate:info")
	public R info(@PathVariable("id") Integer id){
		ThridPlatformGateEntity thridPlatformGate = thridPlatformGateService.queryObject(id);
		
		return R.ok().put("thridPlatformGate", thridPlatformGate);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("thridplatformgate:save")
	public R save(@RequestBody ThridPlatformGateEntity thridPlatformGate){
		thridPlatformGateService.save(thridPlatformGate);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("thridplatformgate:update")
	public R update(@RequestBody ThridPlatformGateEntity thridPlatformGate){
		thridPlatformGateService.update(thridPlatformGate);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("thridplatformgate:delete")
	public R delete(@RequestBody Integer[] ids){
		thridPlatformGateService.deleteBatch(ids);
		
		return R.ok();
	}
	
}
