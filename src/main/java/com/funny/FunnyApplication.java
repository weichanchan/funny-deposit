package com.funny;

import com.funny.admin.agent.service.ThridPlatformGateService;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executor;

@EnableAsync
@EnableCaching
@SpringBootApplication
@MapperScan("com.funny.admin.*.dao")
public class FunnyApplication {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(FunnyApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //设置链接超时时间
        factory.setConnectTimeout(5000);
        //设置读取资料超时时间
        factory.setReadTimeout(5000);
        //设置异步任务（线程不会重用，每次调用时都会重新启动一个新的线程）
        factory.setTaskExecutor(new SimpleAsyncTaskExecutor());
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(factory);
        return asyncRestTemplate;
    }

    /***
     * 创建异步任务执行器
     *
     * @return
     */
    @Bean("taskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //如果池中的实际线程数小于corePoolSize,无论是否其中有空闲的线程，都会给新的任务产生新的线程
        taskExecutor.setCorePoolSize(10);
        //连接池中保留的最大连接数。
        taskExecutor.setMaxPoolSize(30);
        //queueCapacity 线程池所使用的缓冲队列
        taskExecutor.setQueueCapacity(12000);
        //强烈建议一定要给线程起一个有意义的名称前缀，便于分析日志
        taskExecutor.setThreadNamePrefix("common thread-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
