/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author yuhang
 * @Date 2022-09-30 16:19
 * @Description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleResult<T> extends BaseResult{

    private T result;

    public SimpleResult(T result) {
        super(true);
        this.result = result;
    }
}
