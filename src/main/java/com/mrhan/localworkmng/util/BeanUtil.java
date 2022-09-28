/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.util;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.function.Consumer;

/**
 * @Author yuhang
 * @Date 2022-09-28 16:06
 * @Description
 */
public class BeanUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);

    public static <T, R> @Nullable R copy(T source, Class<? extends R> resultClass, Consumer<R> postWrapper) {
        if (source == null) {
            return null;
        }
        R r = null;
        try {
            r = resultClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, r);
        } catch (Exception e) {
            LoggerUtil.warn(LOGGER, e, "[copy](simple copy failed)({},{})",
                    resultClass.getSimpleName(), source);
        }
        if (r != null && postWrapper != null) {
            postWrapper.accept(r);
        }
        return r;
    }

    public static <T, R> @Nullable R copy(T source, Class<? extends R> resultClass) {
        return copy(source, resultClass, null);
    }

}
