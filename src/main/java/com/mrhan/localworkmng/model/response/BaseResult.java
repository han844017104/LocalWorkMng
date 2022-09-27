/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.response;

import com.mrhan.localworkmng.model.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yuhang
 * @Date 2022-09-27 16:00
 * @Description
 */
@Getter
@Setter
public class BaseResult extends ToString {

    private boolean success;

    private String code;

    private String message;

    private Map<String, Object> extInfoMap = new HashMap<>(16);

    public BaseResult() {
    }

    public BaseResult(boolean success) {
        this.success = success;
    }

    public BaseResult(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
