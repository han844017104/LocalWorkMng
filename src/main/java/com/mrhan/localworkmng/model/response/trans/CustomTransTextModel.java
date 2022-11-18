/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.response.trans;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author yuhang
 * @Date 2022-11-17 15:22
 * @Description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomTransTextModel extends TransTextDTO {

    private boolean forceOverride = false;

}
