/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.response.trans;

import com.mrhan.localworkmng.model.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author yuhang
 * @Date 2022-09-30 14:26
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class TransLogGroup extends ToString {

    private String textOriginal;

    private String originalDigest;

    private String fromLanguage;

    private int count;

    private List<TransTextDTO> trnasInfoList;

}
