package com.funny.admin.agent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.common.AbstractController;
import com.funny.admin.system.service.SysUserRoleService;
import com.funny.api.event.notify.FuluCheckEvent;
import com.funny.api.event.notify.a.ACheckEvent;
import com.funny.api.event.notify.superman.SupermanCheckEvent;
import com.funny.api.event.notify.v2.FuluCheckV2Event;
import com.funny.config.FuluConfig;
import com.funny.task.FuluCheckTask;
import com.funny.utils.Des;
import com.funny.utils.PageUtils;
import com.funny.utils.Query;
import com.funny.utils.R;
import com.youzan.open.sdk.util.hash.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 有赞已支付成功订单
 *
 * @author weicc
 * @email
 * @date 2019-02-27 09:05:27
 */
@RestController
@RequestMapping("orderfromyouzan")
public class OrderFromYouzanController extends AbstractController {
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private FuluCheckTask fuluCheckTask;
    @Autowired
    private FuluConfig fuluConfig;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("orderfromyouzan:list")
    public R list(@RequestParam Map<String, Object> params) {
        if (getUser().getUserId() != 1L) {
            params.put("roleIds", sysUserRoleService.queryRoleIdList(getUserId()));
        }
        // 筛选出拼多多的商品
        if ("1".equals(params.get("type"))) {
            params.put("type", "拼多多");
        }
        // 筛选除了拼多多以外的商品
        if ("0".equals(params.get("type"))) {
            params.remove("type");
            params.put("other", "拼多多");
        }
        //查询列表数据
        Query query = new Query(params);

        List<OrderFromYouzanEntity> orderFromYouzanList = orderFromYouzanService.queryList(query);
        int total = orderFromYouzanService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(orderFromYouzanList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 统计
     */
    @RequestMapping("/totalFee")
    @RequiresPermissions("orderfromyouzan:list")
    public R totalFee(String no, String wareNo,
                      String beginTime,
                      String endTime, String type) {
        Map<String, Object> params = new HashMap<>();
        params.put("no", no);
        params.put("wareNo", wareNo);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        params.put("status", 1);
        params.put("type", type);
        if (getUser().getUserId() != 1L) {
            params.put("roleIds", sysUserRoleService.queryRoleIdList(getUserId()));
        }
        // 筛选出拼多多的商品
        if ("1".equals(params.get("type"))) {
            params.put("type", "拼多多");
        }
        // 筛选除了拼多多以外的商品
        if ("0".equals(params.get("type"))) {
            params.remove("type");
            params.put("other", "拼多多");
        }
        //查询列表数据
        BigDecimal totalFee = orderFromYouzanService.queryTotalFee(params);

        return R.ok().put("totalFee", totalFee == null ? 0 : totalFee);
    }


    /**
     * 信息
     */
    @RequestMapping("/cards")
    @RequiresPermissions("orderfromyouzan:info")
    public R cards(Integer id) throws Exception {
        OrderFromYouzanEntity orderFromYouzan = orderFromYouzanService.queryObject(id);
        if (StringUtils.isBlank(orderFromYouzan.getCards())) {
            return R.error("没有可提取的卡密信息。");
        }
        String result = "";
        Map<String, Object> map = objectMapper.readValue(orderFromYouzan.getCards(), Map.class);
        List<Map<String, String>> list = (List<Map<String, String>>) map.get("Cards");
        String key = MD5Utils.MD5(fuluConfig.getAppSecret() + map.get("OrderId")).substring(4, 12);
        for (int i = 0; i < list.size(); i++) {
            String cardNumber = list.get(i).get("CardNumber") == null ? "" : Des.decrypt(list.get(i).get("CardNumber").getBytes("UTF-8"), key);
            String CardPwd = list.get(i).get("CardPwd") == null ? "" : Des.decrypt(list.get(i).get("CardPwd").getBytes("UTF-8"), key);
            result += (i + 1) + "、卡号：" + cardNumber + "，密码：" + CardPwd + "，有效期：" + list.get(i).get("CardDeadline") + "<br />";
        }
        return R.ok().put("cards", result);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("orderfromyouzan:save")
    public R save(@RequestBody OrderFromYouzanEntity orderFromYouzan) {
        orderFromYouzanService.save(orderFromYouzan);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("orderfromyouzan:update")
    public R update(@RequestBody OrderFromYouzanEntity orderFromYouzan) {
        orderFromYouzanService.update(orderFromYouzan);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("orderfromyouzan:delete")
    public R delete(@RequestBody Integer[] ids) {
        orderFromYouzanService.deleteBatch(ids);

        return R.ok();
    }

    /**
     * 查询充值状态
     */
    @RequestMapping("/check")
    @RequiresPermissions("orderfromyouzan:list")
    public R check(@RequestBody Integer[] ids) {
        for (Integer id : ids) {
            OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id);
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.PROCESS);
            orderFromYouzanEntity.setNextRechargeTime(new Date());
            orderFromYouzanService.update(orderFromYouzanEntity);
            fuluCheckTask.publicCheckEvent(orderFromYouzanEntity);
        }
        return R.ok();
    }

}
