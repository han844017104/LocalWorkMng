/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.util;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;

/**
 * @Author yuhang
 * @Date 2022-09-27 17:02
 * @Description
 */
public class LoggerUtil {

    public static void debug(Logger logger, String template, Object... params) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(StrUtil.format(template, params));
        }
    }

    public static void debug(Logger logger, Throwable e, String template, Object... params) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(StrUtil.format(template, params), e);
        }
    }

    public static void info(Logger logger, String template, Object... params) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(StrUtil.format(template, params));
        }
    }

    public static void info(Logger logger, Throwable e, String template, Object... params) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(StrUtil.format(template, params), e);
        }
    }

    public static void warn(Logger logger, String template, Object... params) {
        if (logger != null && logger.isWarnEnabled()) {
            logger.warn(StrUtil.format(template, params));
        }
    }

    public static void warn(Logger logger, Throwable e, String template, Object... params) {
        if (logger != null && logger.isWarnEnabled()) {
            logger.warn(StrUtil.format(template, params), e);
        }
    }

    public static void error(Logger logger, String template, Object... params) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(StrUtil.format(template, params));
        }
    }

    public static void error(Logger logger, Throwable e, String template, Object... params) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(StrUtil.format(template, params), e);
        }
    }

}
