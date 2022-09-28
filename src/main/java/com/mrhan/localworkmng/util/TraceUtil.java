/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author yuhang
 * @Date 2022-09-28 17:38
 * @Description
 */
public class TraceUtil {

    private static final ThreadLocal<TraceContext> TRACE_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    public static String createTrace() {
        long now = new Date().getTime();
        String random = IdUtil.fastSimpleUUID();
        return DateUtil.format(new Date(now), "yyMMddHHmmss")
                + DigestUtils.md5Hex((now + random).getBytes(StandardCharsets.UTF_8)).substring(0, 16);
    }

    public static TraceContext initTraceContext() {
        TraceContext context = new TraceContext();
        context.setTraceStartTime(System.currentTimeMillis());
        context.setTraceId(createTrace());
        TRACE_CONTEXT_THREAD_LOCAL.set(context);
        return context;
    }

    public static TraceContext getTraceContext() {
        return TRACE_CONTEXT_THREAD_LOCAL.get();
    }

    public static String getTraceId() {
        return Optional.ofNullable(TRACE_CONTEXT_THREAD_LOCAL.get()).map(TraceContext::getTraceId).orElse(null);
    }

    public static void cleanTrace() {
        TraceContext context = TRACE_CONTEXT_THREAD_LOCAL.get();
        if (context == null) {
            return;
        }
        TRACE_CONTEXT_THREAD_LOCAL.remove();
        context.destroy();
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    public static class TraceContext {

        private String traceId;

        private Long traceStartTime;

        private Map<String, Object> traceAttributes = new ConcurrentHashMap<>();

        public void destroy() {
            this.traceAttributes.clear();
            this.traceId = null;
            this.traceStartTime = null;
        }
    }

}
