package com.funny.utils.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

/**
 * 邮箱消息发送执行者(邮件配置可以参考{@link MailSenderAutoConfiguration})
 *
 * @author liyanjun
 */
@Component
@ConfigurationProperties("message.mail")
@ConditionalOnProperty(prefix = "message.mail.qq", value = "enabled", matchIfMissing = true)
public class QQEmailMessageSendUtil implements InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(QQEmailMessageSendUtil.class);

    private JavaMailSenderImpl mailSender;

    @Autowired
    private QQMailProperties qqMailProperties;


    @Autowired
    private ObjectProvider<Session> session;

    public JavaMailSenderImpl getMailSender() {
        return mailSender;
    }

    public MailProperties getMailProperties() {
        return qqMailProperties;
    }

    @Override
    public void afterPropertiesSet() {
        Session s = session.getIfAvailable();
        if (s != null) {
            mailSender.setSession(s);
        }
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.qqMailProperties.getHost());
        if (this.qqMailProperties.getPort() != null) {
            mailSender.setPort(this.qqMailProperties.getPort());
        }
        mailSender.setUsername(this.qqMailProperties.getUsername());
        mailSender.setPassword(this.qqMailProperties.getPassword());
        mailSender.setProtocol(this.qqMailProperties.getProtocol());
        if (this.qqMailProperties.getDefaultEncoding() != null) {
            mailSender.setDefaultEncoding(this.qqMailProperties.getDefaultEncoding().name());
        }
        if (!this.qqMailProperties.getProperties().isEmpty()) {
            mailSender.setJavaMailProperties(asProperties(this.qqMailProperties.getProperties()));
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

    /**
     * 发送邮件
     *
     * @param address 对方邮箱地址
     * @param title   邮件标题
     * @param content 邮件内容
     *
     * @return 没有异常即为发送成功，返回 true
     */
    public boolean buildAndSend(String address, String title, String content) throws MessagingException {
        //使用JavaMail的MimeMessage，支付更加复杂的邮件格式和内容  
        MimeMessage msg = getMailSender().createMimeMessage();
        //创建MimeMessageHelper对象，处理MimeMessage的辅助类  
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(getMailProperties().getUsername());
        helper.setTo(address);
        helper.setSubject(title);
        helper.setText(content,true);
        getMailSender().send(msg);
        return true;
    }

}
