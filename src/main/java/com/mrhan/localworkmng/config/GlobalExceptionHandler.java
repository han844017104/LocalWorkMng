/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.config;

import com.mrhan.localworkmng.model.enums.ResultCode;
import com.mrhan.localworkmng.model.exception.BizException;
import com.mrhan.localworkmng.model.response.BaseResult;
import com.mrhan.localworkmng.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author yuhang
 * @Date 2022-09-27 17:01
 * @Description
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public BaseResult commonErrorHandler(HttpServletRequest req, Exception e) {
        LoggerUtil.error(LOGGER, e, "(uncaught exception)({})", req.getServletPath());
        if (e instanceof BizException) {
            return new BaseResult(((BizException) e).getErrorCode(), ((BizException) e).getErrorMessage());
        }
        return new BaseResult(ResultCode.UNKNOWN.getCode(),
                ResultCode.UNKNOWN.getMessage() + ": " + e.getMessage());
    }

}
