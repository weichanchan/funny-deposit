package com.funny.admin.agent.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.utils.PageUtils;
import com.funny.utils.Query;
import com.funny.utils.R;


/**
 * 有赞已支付成功订单
 * 
 * @author weicc
 * @email 
 * @date 2019-02-27 09:05:27
 */
@RestController
@RequestMapping("orderfromyouzan")
public class OrderFromYouzanController {
	@Autowired
	private OrderFromYouzanService orderFromYouzanService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("orderfromyouzan:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<OrderFromYouzanEntity> orderFromYouzanList = orderFromYouzanService.queryList(query);
		int total = orderFromYouzanService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(orderFromYouzanList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("orderfromyouzan:info")
	public R info(@PathVariable("id") Integer id){
		OrderFromYouzanEntity orderFromYouzan = orderFromYouzanService.queryObject(id);
		
		return R.ok().put("orderFromYouzan", orderFromYouzan);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("orderfromyouzan:save")
	public R save(@RequestBody OrderFromYouzanEntity orderFromYouzan){
		orderFromYouzanService.save(orderFromYouzan);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("orderfromyouzan:update")
	public R update(@RequestBody OrderFromYouzanEntity orderFromYouzan){
		orderFromYouzanService.update(orderFromYouzan);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("orderfromyouzan:delete")
	public R delete(@RequestBody Integer[] ids){
		orderFromYouzanService.deleteBatch(ids);
		
		return R.ok();
	}
	
}
