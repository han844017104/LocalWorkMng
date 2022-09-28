/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.util;

import com.mrhan.localworkmng.model.response.PageResult;
import org.springframework.beans.BeanUtils;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author yuhang
 * @Date 2022-09-28 16:31
 * @Description
 */
public class PageModelUtil {

    public static <T, R> PageResult<R> transformResult(PageResult<T> result, Function<T, R> converter) {
        if (result == null) {
            return null;
        }
        PageResult<R> transformResult = new PageResult<>();
        BeanUtils.copyProperties(result, transformResult);
        transformResult.setResults(
                Optional.ofNullable(result.getResults()).map(
                        list -> list.stream().map(converter).collect(Collectors.toList())
                ).orElse(null)
        );
        return transformResult;
    }

}
