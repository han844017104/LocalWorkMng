/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.schedule;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @Author yuhang
 * @Date 2022-12-20 15:17
 * @Description
 */
@Component
public class SchedulerRunner {

    @Resource
    protected ThreadPoolTaskExecutor commonSchedulerThreadPool;


}
