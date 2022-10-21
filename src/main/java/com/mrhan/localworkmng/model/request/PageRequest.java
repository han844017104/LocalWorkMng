/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.request;

import com.mrhan.localworkmng.model.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.function.Consumer;

/**
 * @Author yuhang
 * @Date 2022-09-27 16:27
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class PageRequest<T> extends ToString {

    private boolean paged;

    private long total;

    private long currentPage;

    private long size;

    private T condition;

    public PageRequest<T> withCondition(T condition) {
        this.condition = condition;
        return this;
    }

    public PageRequest<T> condWrap(Consumer<T> doForCondition) {
        if (condition == null) {
            return this;
        }
        doForCondition.accept(condition);
        return this;
    }

}
