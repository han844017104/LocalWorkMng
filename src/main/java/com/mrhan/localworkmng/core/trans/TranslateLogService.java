/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.core.trans;

import com.mrhan.localworkmng.model.bo.TranslateFrequentWord;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.model.response.trans.TransLogGroup;

/**
 * @Author yuhang
 * @Date 2022-09-30 14:40
 * @Description
 */
public interface TranslateLogService {

    PageResult<TranslateFrequentWord> queryFrequentWords(PageRequest<TransLogGroup> request);

    PageResult<TranslateFrequentWord> queryFrequentWordsByCache(PageRequest<TransLogGroup> request);

    String getCacheVersion();

    void setCacheVersion(String newVersion);

    String newCacheVersion();

}
