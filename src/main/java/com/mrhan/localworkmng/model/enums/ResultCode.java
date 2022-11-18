/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.enums;

import lombok.Getter;

/**
 * @Author yuhang
 * @Date 2022-09-27 17:35
 * @Description
 */
@Getter
public enum ResultCode {
    UNKNOWN("UNKNOWN_ERROR", "未知系统异常"),

    ILLEGAL_ARGUMENT("ILLEGAL_ARGUMENT", "非法参数"),

    DATA_EXISTED("DATA_EXISTED", "数据已存在"),
    ;

    private final String code;

    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
