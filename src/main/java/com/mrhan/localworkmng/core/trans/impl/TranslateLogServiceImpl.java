/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.core.trans.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.mrhan.localworkmng.core.trans.TranslateLogService;
import com.mrhan.localworkmng.dal.trans.mapper.TranslateLogMapper;
import com.mrhan.localworkmng.dal.trans.model.TranslateLogDO;
import com.mrhan.localworkmng.integration.redis.RedisClient;
import com.mrhan.localworkmng.model.bo.TranslateFrequentWord;
import com.mrhan.localworkmng.model.constance.RedisConstance;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.model.response.trans.TransLogGroup;
import com.mrhan.localworkmng.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslateLogServiceImpl.class);

    @Resource
    private TranslateLogMapper translateLogMapper;

    @Resource
    private RedisClient redisClient;

    private String cacheVersion;

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
//                        .having("count > {0}", "1")
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

    @Override
    public PageResult<TranslateFrequentWord> queryFrequentWordsByCache(PageRequest<TransLogGroup> request) {
        long start = request.isPaged() ? (request.getCurrentPage() - 1L) * request.getSize() : 0L;
        long end = request.isPaged() ? start + request.getSize() - 1L : -1L;
        String key = RedisConstance.TRANS_HOT_WORDS_CACHE_PREFIX + cacheVersion;
        long count = redisClient.zSetGetCount(key);
        List<TranslateFrequentWord> list = redisClient.zSetGetJsonObj(key, start, end,
                TranslateFrequentWord.class);
        PageResult<TranslateFrequentWord> result = new PageResult<>();
        result.setSize(request.getSize());
        result.setTotal(count);
        result.setCurrentPage(request.getCurrentPage());
        result.setSuccess(true);
        result.setPaged(request.isPaged());
        result.setResults(list);
        return result;
    }

    @Override
    public String getCacheVersion() {
        return this.cacheVersion;
    }

    @Override
    public void setCacheVersion(String newVersion) {
        LoggerUtil.info(LOGGER, "[refreshVersion]({},{})", this.cacheVersion, newVersion);
        this.cacheVersion = newVersion;
    }

    @Override
    public String newCacheVersion() {
        return DateUtil.format(new Date(), "yyyy-MM-dd_HH-mm-ss");
    }

}
