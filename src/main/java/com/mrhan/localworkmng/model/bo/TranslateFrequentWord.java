/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.bo;

import lombok.Data;

/**
 * @Author yuhang
 * @Date 2022-09-30 14:41
 * @Description
 */
@Data
public class TranslateFrequentWord {

    private String originalDigest;

    private String fromLanguage;

    private int count;

}
