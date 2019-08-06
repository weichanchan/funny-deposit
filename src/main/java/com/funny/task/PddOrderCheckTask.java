package com.funny.task;

import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.ThridPddConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 拼多多平台订单主动查询任务
 *
 * @author liyanjun
 */
@Component("pddOrderCheckTask")
public class PddOrderCheckTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private ApplicationContext applicationContext;

    public void watch() {

    }

}
