/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Author yuhang
 * @Date 2022-12-20 15:13
 * @Description
 */
@Configuration
public class CommonSchedulerThreadPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor commonSchedulerThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);
        return executor;
    }

}
