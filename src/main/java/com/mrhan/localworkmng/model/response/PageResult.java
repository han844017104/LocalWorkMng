/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author yuhang
 * @Date 2022-09-27 16:58
 * @Description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageResult<T> extends BaseResult {

    private boolean isPaged;

    private long total;

    private long currentPage;

    private long size;

    private List<T> results;

}
