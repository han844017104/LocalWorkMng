/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.schedule.interceptor;

import com.mrhan.localworkmng.util.TraceUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author yuhang
 * @Date 2022-12-20 15:27
 * @Description
 */
@Aspect
@Component
public class CommonScheduleInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonScheduleInterceptor.class);

    @Around(value = "@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object around(ProceedingJoinPoint point) {
        TraceUtil.initTraceContext();
        try {
            return point.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            TraceUtil.cleanTrace();
        }
    }


}
