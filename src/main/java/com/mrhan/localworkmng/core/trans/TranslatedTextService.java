/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.core.trans;

import com.mrhan.localworkmng.model.bo.TranslatedTextBO;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.request.trans.TransTextQueryParam;
import com.mrhan.localworkmng.model.response.PageResult;

/**
 * @Author yuhang
 * @Date 2022-08-22 11:04
 * @Description
 */
public interface TranslatedTextService {

    PageResult<TranslatedTextBO> query(PageRequest<TransTextQueryParam> request);

    boolean updateTransText(TranslatedTextBO bo);

    TranslatedTextBO upsertTransText(TranslatedTextBO bo, boolean override);

}
