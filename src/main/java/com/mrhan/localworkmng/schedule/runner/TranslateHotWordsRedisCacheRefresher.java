/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.schedule.runner;

import com.google.common.collect.Lists;
import com.mrhan.localworkmng.core.trans.TranslateLogService;
import com.mrhan.localworkmng.integration.redis.RedisClient;
import com.mrhan.localworkmng.model.bo.TranslateFrequentWord;
import com.mrhan.localworkmng.model.constance.RedisConstance;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.model.response.trans.TransLogGroup;
import com.mrhan.localworkmng.schedule.SchedulerRunner;
import com.mrhan.localworkmng.util.JsonUtil;
import com.mrhan.localworkmng.util.LoggerUtil;
import com.mrhan.localworkmng.util.TimeMeter;
import com.mrhan.localworkmng.util.ValidateUtil;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;

/**
 * @Author yuhang
 * @Date 2022-12-20 15:21
 * @Description
 */
@Component
public class TranslateHotWordsRedisCacheRefresher extends SchedulerRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslateHotWordsRedisCacheRefresher.class);

    @Resource
    private RedisClient redisClient;

    @Resource
    private TranslateLogService translateLogService;

    @PostConstruct
    public void init() {
        LoggerUtil.info(LOGGER, "[startup](init redis cache for hot words)");
        doRefreshCache();
        LoggerUtil.info(LOGGER, "[startup](init redis cache for hot words success!)");
    }

    @Scheduled(cron = "20 0/1 * * * ? ")
    public void doRefreshCache() {
        LoggerUtil.info(LOGGER, "[schedule](hot words refresh)(start)");
        PageRequest<TransLogGroup> request = new PageRequest<>();
        request.setPaged(true);
        request.setSize(Long.MAX_VALUE);
        request.setCurrentPage(1L);
        PageResult<TranslateFrequentWord> result = translateLogService.queryFrequentWords(request);
        LoggerUtil.info(LOGGER, "[schedule](hot words refresh)(load all words)({})", result.getResults().size());
        List<Pair<String, Double>> list = Lists.newArrayListWithCapacity(result.getResults().size());
        for (int i = 0; i < result.getResults().size(); i++) {
            list.add(MutablePair.of(JsonUtil.toJsonString(result.getResults().get(i)),
                    Double.valueOf(String.valueOf(i))));
        }
        String oldVersion = translateLogService.getCacheVersion();
        String newVersion = translateLogService.newCacheVersion();
        TimeMeter.mete(() -> {
            redisClient.zSetBatchSet(RedisConstance.TRANS_HOT_WORDS_CACHE_PREFIX + newVersion,
                    list, 1000 * 60 * 30);
            return null;
        }, "writeCache");
        translateLogService.setCacheVersion(newVersion);
        if (oldVersion != null) {
            LoggerUtil.info(LOGGER, "[schedule](hot words refresh)(start clean old cache)({})", oldVersion);
            boolean remove = redisClient.remove(RedisConstance.TRANS_HOT_WORDS_CACHE_PREFIX + oldVersion);
            ValidateUtil.checkTrue(remove, "remove old version cache failed:" + oldVersion);
        } else {
            LoggerUtil.info(LOGGER, "[schedule](hot words refresh)(skip clean old cache)");
        }

    }

    @PreDestroy
    public void clear() {
        String cacheVersion = translateLogService.getCacheVersion();
        LoggerUtil.info(LOGGER, "[clear](hot words refresh)(clean current cache)({})", cacheVersion);
        if (cacheVersion != null) {
            redisClient.remove(RedisConstance.TRANS_HOT_WORDS_CACHE_PREFIX + cacheVersion);
        }
    }

}
