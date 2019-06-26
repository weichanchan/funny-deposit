package com.funny.config;

import com.funny.utils.SignUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author liyanjun
 */
@Component
@ConditionalOnProperty("optional.superman.enable")
public class SupermanConfig {
    @Value("${optional.superman.url}")
    private String url;
    @Value("${optional.superman.username}")
    private String username;
    @Value("${optional.superman.password}")
    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() throws IOException {
        String token = "user=" + username + "&&pass=" + SignUtils.getMD5(password);
        return SignUtils.getMD5(token);
    }
}
