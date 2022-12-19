/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @Author yuhang
 * @Date 2022-12-19 17:33
 * @Description
 */
public class TimeMeter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeMeter.class);

    public static <R> R mete(Supplier<R> supplier, String code) {
        long start = System.currentTimeMillis();
        boolean success = true;
        try {
            return supplier.get();
        } catch (Throwable e) {
            success = false;
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            LoggerUtil.info(LOGGER, "[mete][{}]({},{})",
                    code, success ? "Y" : "N", end - start);
        }
    }

}
