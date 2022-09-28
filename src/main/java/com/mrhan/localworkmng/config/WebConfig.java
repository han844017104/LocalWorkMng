/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.config;

import com.mrhan.localworkmng.util.LoggerUtil;
import com.mrhan.localworkmng.util.TraceUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @Author yuhang
 * @Date 2022-09-28 17:05
 * @Description
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger("SAL");

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                             Object handler) throws Exception {
                        boolean passed = HandlerInterceptor.super.preHandle(request, response, handler);
                        if (passed) {
                            if (handler instanceof HandlerMethod) {
                                TraceUtil.initTraceContext();
                                response.addHeader("request-id", TraceUtil.getTraceId());
                            }
                        }
                        return passed;
                    }

                    @Override
                    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                                           ModelAndView modelAndView) throws Exception {
                        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
                    }

                    @Override
                    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                                Object handler, Exception ex) throws Exception {
                        if (handler instanceof HandlerMethod handlerMethod) {
                            String controllerName = handlerMethod.getBeanType().getSimpleName();
                            String method = handlerMethod.getMethod().getName();
                            TraceUtil.TraceContext context = TraceUtil.getTraceContext();
                            String traceId = Optional.ofNullable(context).map(
                                    TraceUtil.TraceContext::getTraceId).orElse("-");
                            Long start = Optional.ofNullable(context).map(
                                    TraceUtil.TraceContext::getTraceStartTime).orElse(null);
                            LoggerUtil.info(LOGGER, "[{}][{}.{}]({},{})", traceId, controllerName, method,
                                    start == null ? 0 : System.currentTimeMillis() - start, ex == null ? "Y" : "N");
                        }
                        TraceUtil.cleanTrace();
                    }
                }).addPathPatterns("/api/**");
    }

    @Override
    public void configurePathMatch(@NotNull PathMatchConfigurer configurer) {
        WebMvcConfigurer.super.configurePathMatch(configurer);
        configurer.addPathPrefix("/api", e ->
                e.isAnnotationPresent(Controller.class) || e.isAnnotationPresent(RestController.class));
    }
}
