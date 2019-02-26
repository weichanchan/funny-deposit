package com.funny.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.NotifyResendRecordEntity;
import com.funny.admin.agent.service.AgentOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * 有赞通知订单，福禄下单状态检查任务
 * 
 * resendTask为spring bean的名称
 * 
 * @author liyanjun
 */
@Component("fuluCheckTask")
public class FuluCheckTask {
	private Logger logger = LoggerFactory.getLogger(getClass());
	

	
	public void watch(){

	}
}
