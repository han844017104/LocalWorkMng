/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.request.trans;

import com.mrhan.localworkmng.model.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author yuhang
 * @Date 2022-09-28 15:53
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class TransTextQueryParam extends ToString {

    private List<String> originalDigestList;

    private List<String> originalDigestAndFromList;

    private String origin;

    private String trans;

    private String from;

    private String to;

    private List<String> engines;

    private String fuzzyStr;

}
