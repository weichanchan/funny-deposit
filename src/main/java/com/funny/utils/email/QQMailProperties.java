package com.funny.utils.email;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liyanjun
 */
@Component
@ConfigurationProperties(prefix = "message.mail.qq")
public class QQMailProperties extends MailProperties {

}
