/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.util;

import cn.hutool.core.util.StrUtil;
import com.mrhan.localworkmng.model.enums.ResultCode;
import com.mrhan.localworkmng.model.exception.BizException;

/**
 * @Author yuhang
 * @Date 2022-09-30 14:32
 * @Description
 */
public class ValidateUtil {

    public static void checkNotNull(Object obj, String message) {
        if (obj == null) {
            throw new BizException(ResultCode.ILLEGAL_ARGUMENT.getCode(),
                    StrUtil.isNotBlank(message) ? message : ResultCode.ILLEGAL_ARGUMENT.getMessage());
        }
    }

    public static void checkNotNull(Object obj) {
        checkNotNull(obj, null);
    }

    public static void checkNotBlank(String str, String message) {
        if (StrUtil.isBlank(str)) {
            throw new BizException(ResultCode.ILLEGAL_ARGUMENT.getCode(),
                    StrUtil.isNotBlank(message)? message : ResultCode.ILLEGAL_ARGUMENT.getMessage());
        }
    }

    public static void checkNotBlank(String str) {
        checkNotBlank(str, null);
    }

    public static void checkTrue(boolean expression, String message) {
        if (!expression) {
            throw new BizException(ResultCode.ILLEGAL_ARGUMENT.getCode(),
                    StrUtil.isNotBlank(message)? message : ResultCode.ILLEGAL_ARGUMENT.getMessage());
        }
    }

    public static void checkTrue(boolean expression) {
        checkTrue(expression, null);
    }
}
