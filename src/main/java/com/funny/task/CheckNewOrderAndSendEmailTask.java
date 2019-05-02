package com.funny.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.NotifyResendRecordEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.system.service.SysUserRoleService;
import com.funny.utils.R;
import com.funny.utils.email.QQEmailMessageSendUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检查是否有3分钟后还没处理的订单定时任务，如果有发送邮件
 *
 * @author liyanjun
 */
//@Component("checkNewOrderAndSendEmailTask")
public class CheckNewOrderAndSendEmailTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AgentOrderService agentOrderService;

    @Autowired
    private QQEmailMessageSendUtil qqEmailMessageSendUtil;

    @Autowired
    RestTemplate template;

    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 记录ID，同一条提醒不要发送2次
     */
    private long id;

    public void watch() throws MessagingException {
        Map<String, Object> params = new HashMap<>(16);
        AgentOrderEntity agentOrder = agentOrderService.queryLast(params);
        Long now = System.currentTimeMillis();
        now = now - 180 * 1000;
        if (agentOrder.getCreateTime().getTime() >= now && agentOrder.getStatus() == 1 && id != agentOrder.getId()) {
            id = agentOrder.getId();
            // 180没有处理，发送邮件。
            String content = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"640\" style=\"margin:0 auto;color:#555; font:16px/26px '微软雅黑','宋体',Arail; \">\n" +
                    "    <tbody>\n" +
                    "    <tr style=\"background-color:#fff;\">\n" +
                    "        <td style=\"padding:10px 38px;\">\n" +
                    "            <div style=\"margin:20px 0;font-size:13px;\">\n" +
                    "                您有新的订单需要处理，订单号为:<span style=\"color:#019875;\">" + agentOrder.getJdOrderNo() + "</span>。\n" +
                    "            </div>\n" +
                    "            <div style=\"color:#c5c5c5; font-size:12px; border-top:1px solid #e6e6e6; padding:7px 0; line-height:20px;\">\n" +
                    "                本邮件是系统自动发出\n" +
                    "            </div>\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    </tbody>\n" +
                    "</table>";
            qqEmailMessageSendUtil.buildAndSend("10037573@qq.com", "您有新的订单需要处理", content);
        }

    }
}
