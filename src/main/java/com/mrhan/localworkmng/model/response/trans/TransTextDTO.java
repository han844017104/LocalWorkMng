/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.response.trans;

import com.mrhan.localworkmng.model.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author yuhang
 * @Date 2022-09-28 15:58
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class TransTextDTO extends ToString {

    private Long id;

    private Date gmtCreate;

    private String fromLanguage;

    private String toLanguage;

    private String textOriginal;

    private String textTrans;

    private String transEngine;

    private String originalDigest;

    private String transDigest;

    private String extInfo;

}
