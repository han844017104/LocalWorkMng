/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.exception;

import com.mrhan.localworkmng.model.enums.ResultCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author yuhang
 * @Date 2022-09-27 17:11
 * @Description
 */
@EqualsAndHashCode(callSuper = true)
public class BizException extends RuntimeException {

    @Getter
    @Setter
    private String errorCode;

    @Getter
    @Setter
    private String errorMessage;

    public BizException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BizException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public BizException(String errorCode, String errorMessage, Exception e) {
        super(errorMessage, e);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BizException(String errorMessage, Exception e) {
        super(errorMessage, e);
        this.errorMessage = errorMessage;
    }

    public BizException(ResultCode resultCode) {
        super(resultCode.getCode() + ":" + resultCode.getMessage());
        this.errorMessage = resultCode.getMessage();
        this.errorCode = resultCode.getCode();
    }

    public BizException(ResultCode resultCode, Exception e) {
        super(resultCode.getCode() + ":" + resultCode.getMessage(), e);
        this.errorMessage = resultCode.getMessage();
        this.errorCode = resultCode.getCode();
    }
}
