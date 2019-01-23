package com.funny;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

@EnableAsync
@EnableCaching
@SpringBootApplication
@MapperScan("com.funny.admin.*.dao")
public class FunnyApplication {

	public static void main(String[] args) {
		SpringApplication.run(FunnyApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
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
		taskExecutor.setCorePoolSize(2);
		//连接池中保留的最大连接数。
		taskExecutor.setMaxPoolSize(15);
		//queueCapacity 线程池所使用的缓冲队列
		taskExecutor.setQueueCapacity(6000);
		//强烈建议一定要给线程起一个有意义的名称前缀，便于分析日志
		taskExecutor.setThreadNamePrefix("common thread-");
		taskExecutor.initialize();
		return taskExecutor;
	}
}
