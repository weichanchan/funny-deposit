package com.funny.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.NotifyResendRecordEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.system.entity.SysUserEntity;
import com.funny.admin.system.service.SysUserService;
import com.funny.utils.ConfigUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 通知重发定时任务
 * 
 * resendTask为spring bean的名称
 * 
 * @author liyanjun
 */
@Component("resendTask")
public class ResendTask {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AgentOrderService  agentOrderService;

	@Autowired
	RestTemplate template;

	ObjectMapper objectMapper = new ObjectMapper();
	
//	public void test(String params){
//		logger.info("我是带参数的test方法，正在被执行，参数为：" + params);
//
//		try {
//			Thread.sleep(1000L);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		SysUserEntity user = sysUserService.queryObject(1L);
//		System.out.println(ToStringBuilder.reflectionToString(user));
//
//	}
	
	
	public void watch(){
		List<NotifyResendRecordEntity> notifyResendRecordEntities = agentOrderService.findResendTask();
		for (NotifyResendRecordEntity notifyResendRecordEntity: notifyResendRecordEntities) {
			ResponseEntity<String> response = template.postForEntity(notifyResendRecordEntity.getNotifyUrl(), null, String.class);
			Map result;
			try {
				logger.debug(response.getBody());
				result = objectMapper.readValue(response.getBody(), Map.class);
				String flag = (String) result.get("isSuccess");
				if ("T".equals(flag)) {
					return;
				}
			} catch (IOException e) {
				logger.error(response.getBody(), e);
			}
			// 进行下一次重试
			agentOrderService.resend(notifyResendRecordEntity);
		}
	}
}
