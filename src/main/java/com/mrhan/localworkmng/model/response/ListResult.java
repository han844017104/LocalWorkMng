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
 * @Date 2022-09-27 16:24
 * @Description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ListResult<T> extends BaseResult {

    private List<T> results;

}
