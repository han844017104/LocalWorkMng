/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.core.trans.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.mrhan.localworkmng.core.trans.TranslateLogService;
import com.mrhan.localworkmng.dal.trans.mapper.TranslateLogMapper;
import com.mrhan.localworkmng.dal.trans.model.TranslateLogDO;
import com.mrhan.localworkmng.model.bo.TranslateFrequentWord;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.model.response.trans.TransLogGroup;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author yuhang
 * @Date 2022-09-30 14:40
 * @Description
 */
@Service
public class TranslateLogServiceImpl implements TranslateLogService {

    @Resource
    private TranslateLogMapper translateLogMapper;

    @Override
    public PageResult<TranslateFrequentWord> queryFrequentWords(PageRequest<TransLogGroup> request) {
        Page<Map<String, Object>> page = new Page<>();
        if (request.isPaged()) {
            page.setCurrent(request.getCurrentPage());
            page.setSize(request.getSize());
        }
        Page<Map<String, Object>> logs = translateLogMapper.selectMapsPage(page,
                new QueryWrapper<TranslateLogDO>()
                        .select("count(*) as count", "original_digest", "from_lang")
                        .groupBy("original_digest", "from_lang")
                        .orderByDesc("count")
        );
        PageResult<TranslateFrequentWord> result = new PageResult<>();
        result.setSuccess(true);
        result.setPaged(request.isPaged());
        result.setTotal(logs.getTotal());
        result.setCurrentPage(logs.getCurrent());
        result.setSize(logs.getSize());
        result.setResults(
                Optional.ofNullable(logs.getRecords()).orElse(Lists.newArrayList()).stream()
                        .map(map -> {
                            TranslateFrequentWord word = new TranslateFrequentWord();
                            word.setCount(Integer.parseInt(String.valueOf(map.get("count"))));
                            word.setOriginalDigest(String.valueOf(map.get("original_digest")));
                            word.setFromLanguage(String.valueOf(map.get("from_lang")));
                            return word;
                        }).collect(Collectors.toList())
        );
        return result;
    }
}
