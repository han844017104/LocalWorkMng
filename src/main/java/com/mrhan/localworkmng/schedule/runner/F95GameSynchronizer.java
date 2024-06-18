package com.mrhan.localworkmng.schedule.runner;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mrhan.localworkmng.config.F95BizConfiguration;
import com.mrhan.localworkmng.config.ProxyConfiguration;
import com.mrhan.localworkmng.dal.f95.mapper.F95GameMapper;
import com.mrhan.localworkmng.dal.f95.mapper.F95GameRelationMapper;
import com.mrhan.localworkmng.dal.f95.model.F95Game;
import com.mrhan.localworkmng.dal.f95.model.F95GameRelation;
import com.mrhan.localworkmng.model.enums.F95RelationTypeEnum;
import com.mrhan.localworkmng.schedule.SchedulerRunner;
import com.mrhan.localworkmng.util.LoggerUtil;
import com.mrhan.localworkmng.util.ValidateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author yuhang
 * @Date 2024-06-08 15:05
 * @Description
 */
@Component
public class F95GameSynchronizer extends SchedulerRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(F95GameSynchronizer.class);

    @Resource
    private ProxyConfiguration proxyConfiguration;

    @Resource
    private F95GameMapper f95GameMapper;

    @Resource
    private F95BizConfiguration f95BizConfiguration;

    @Resource
    private F95GameRelationMapper f95GameRelationMapper;

    @Scheduled(cron = "0 0/15 * * * ? ")
    public void doSyncF95Fame() {
        LoggerUtil.info(LOGGER, "[f95Game-sync](start sync)");
        Map<Integer, JSONArray> container = new ConcurrentHashMap<>();
        int maxPage = doQuery(1, container);
        LoggerUtil.info(LOGGER, "[f95Game-sync](total page)({})", maxPage);
        RateLimiter rateLimiter = RateLimiter.create(Double.parseDouble(StrUtil.blankToDefault(f95BizConfiguration.getGameSyncLimiter(), "5")));
        List<CompletableFuture<Void>> tasks = Lists.newArrayList();
        for (int page = 2; page <= maxPage; page++) {
            int finalPage = page;
            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                rateLimiter.acquire();
                LoggerUtil.info(LOGGER, "[doSyncF95Fame](start to loading page)({})", finalPage);
                boolean s = true;
                long start = System.currentTimeMillis();
                try {
                    doQuery(finalPage, container);
                } catch (Exception e) {
                    s = false;
                    LoggerUtil.error(LOGGER, e, "[doSyncF95Fame](sync failed)({})", finalPage);
                } finally {
                    LoggerUtil.info(LOGGER, "[doSyncF95Fame](do page sync finished)({})({},{})", finalPage,
                            s ? "Y" : "N", System.currentTimeMillis() - start);
                }
            }, commonSchedulerThreadPool);
            tasks.add(task);
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[]{})).join();
        List<Pair<F95Game, List<F95GameRelation>>> list = fixGameUpdateDateAndTransform(container);
        LoggerUtil.info(LOGGER, "[f95Game-sync](load game finished)({})", list.size());
        saveAll(list);
    }

    private List<Pair<F95Game, List<F95GameRelation>>> fixGameUpdateDateAndTransform(Map<Integer, JSONArray> container) {
        AtomicLong lastTime = new AtomicLong(Long.MAX_VALUE);
        long currentTime = System.currentTimeMillis();
        List<Pair<F95Game, List<F95GameRelation>>> list = Lists.newLinkedList();
        Lists.newArrayList(container.keySet()).stream().sorted().forEach(page -> {
            JSONArray jsonArray = container.get(page);
            if (CollectionUtils.isEmpty(jsonArray)) {
                return;
            }
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject one = jsonArray.getJSONObject(i);
                String date = one.getString("date");
                long time = parse2time(currentTime, date, lastTime.get());
                lastTime.set(time);
                one.put("parsedTime", time);
                list.add(transform(one));
            }
        });
        return list;
    }

    private Pair<F95Game, List<F95GameRelation>> transform(JSONObject one) {
        F95Game game = new F95Game();
        List<F95GameRelation> relations = Lists.newArrayList();
        game.setThreadId(one.getString("thread_id"));
        game.setTitle(one.getString("title"));
        game.setGameVersion(one.getString("version"));
        game.setViews(one.getLongValue("views"));
        game.setLikes(one.getLongValue("likes"));
        game.setLikeRatio(game.getViews() == 0L ?
                BigDecimal.ZERO :
                new BigDecimal(game.getLikes())
                        .divide(new BigDecimal(game.getViews()), RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100))
                        .setScale(2, RoundingMode.HALF_UP));
        game.setRating(new BigDecimal(one.getString("rating")));
        game.setGameUpdateDate(new Date(one.getLongValue("parsedTime")));
        JSONObject ext = new JSONObject();
        ext.put("originData", one);
        game.setExtInfo(ext.toJSONString());

        JSONArray prefixes = one.getJSONArray("prefixes");
        if (CollectionUtils.isNotEmpty(prefixes)) {
            for (int i = 0; i < prefixes.size(); i++) {
                String val = prefixes.getString(i);
                relations.add(new F95GameRelation(null, val, game.getThreadId(), F95RelationTypeEnum.PREFIX.getCode()));
            }
        }
        JSONArray tags = one.getJSONArray("tags");
        if (CollectionUtils.isNotEmpty(tags)) {
            for (int i = 0; i < tags.size(); i++) {
                String val = tags.getString(i);
                relations.add(new F95GameRelation(null, val, game.getThreadId(), F95RelationTypeEnum.TAG.getCode()));
            }
        }
        return MutablePair.of(game, relations);
    }

    private static final Splitter SPLITTER = Splitter.on(" ").omitEmptyStrings().trimResults();

    private long parse2time(long beginTime, String date, long lastTime) {
        int offset;
        DateField timeUnit;
        List<String> strings = SPLITTER.splitToList(date);
        if (strings.size() == 2) {
            offset = Integer.parseInt(strings.get(0));
            switch (strings.get(1)) {
                case "min", "mins" -> timeUnit = DateField.MINUTE;
                case "hrs", "hr" -> timeUnit = DateField.HOUR;
                case "days" -> timeUnit = DateField.DAY_OF_YEAR;
                case "week", "weeks" -> timeUnit = DateField.WEEK_OF_YEAR;
                case "month", "months" -> timeUnit = DateField.MONTH;
                case "year", "years" -> timeUnit = DateField.YEAR;
                default -> throw new RuntimeException("Unknown time unit: " + strings.get(1) + "【" + date + "】");
            }
        } else if (strings.size() == 1) {
            offset = 1;
            switch (strings.get(0)) {
                case "Yesterday" -> timeUnit = DateField.DAY_OF_YEAR;
                default -> throw new RuntimeException("Unknown time: " + date);
            }
        } else {
            throw new RuntimeException("Unknown datetime: " + date);
        }
        DateTime offsetTime = DateUtil.offset(new Date(beginTime), timeUnit, -offset);
        if (offsetTime.getTime() >= lastTime) {
            DateField fixedOffsetUnit;
            switch (timeUnit) {
                case MINUTE -> fixedOffsetUnit = DateField.SECOND;
                default -> fixedOffsetUnit = DateField.MINUTE;
            }
            return DateUtil.offset(new Date(lastTime), fixedOffsetUnit, -1).getTime();
        }
        return offsetTime.getTime();
    }

    private Integer doQuery(int page, Map<Integer, JSONArray> container) {
        HttpRequest get = HttpUtil.createGet(StrUtil.format("https://f95zone.to/sam/latest_alpha/latest_data.php?cmd=list&cat=games&page={}&sort=date&rows=90", page));
        get.setHttpProxy(proxyConfiguration.getHost(), Integer.parseInt(proxyConfiguration.getPort()));
        get.cookie("__ddgid_=I1Q0KyhLm6EVQzZO; __ddg2_=RG6RNUVRQ2FyCtEQ; __ddg1_=qZLtSjeFiW1UBBwXIb3K; xf_user=103048^%^2C5hcBsKBC7sJW1-Frgkephvf32rUf4NqsnC3evk-_; _ga_HE9XJLVKML=deleted; _gid=GA1.2.691960919.1718196055; xf_csrf=h-eEd_AyZm5NfC5S; xf_session=Rk0PjdyoQ4TaojhperKNlF-RyATUD266; _ga_HE9XJLVKML=GS1.1.1718282698.691.0.1718282698.60.0.0; _ga=GA1.2.1225038220.1677928299^");
        HttpResponse execute = get.execute();
        ValidateUtil.checkTrue(execute.isOk(), "request is not ok: " + execute.getStatus());
        JSONObject body = JSON.parseObject(execute.body());
        LoggerUtil.info(LOGGER, "[doSyncF95Fame](resp body)({})", execute);
        ValidateUtil.checkTrue(MapUtils.isNotEmpty(body), "body is empty");
        String string = body.getString("status");
        ValidateUtil.checkTrue("ok".equals(string), "status is not ok: " + string + "-" + body.toJSONString());
        JSONObject pagination = body.getJSONObject("msg").getJSONObject("pagination");
        JSONArray jsonArray = body.getJSONObject("msg").getJSONArray("data");
        container.put(page, jsonArray);
        return pagination.getInteger("total");
    }

    private void save(Pair<F95Game, List<F95GameRelation>> pair) {
        f95GameMapper.upsert(pair.getLeft());
        if (CollectionUtils.isNotEmpty(pair.getRight())) {
            f95GameRelationMapper.batchUpsert(pair.getRight());
        }
    }


    private void saveAll(List<Pair<F95Game, List<F95GameRelation>>> all) {
        List<F95Game> games = all.stream().map(Pair::getLeft).toList();
        List<F95GameRelation> relations = all.stream().map(Pair::getRight).flatMap(Collection::stream).toList();
        List<List<F95Game>> partition = Lists.partition(games, 2000);
        for (int i = 0; i < partition.size(); i++) {
            LoggerUtil.info(LOGGER, "[f95-game-save](saving game partition)({}/{})", i + 1, partition.size());
            List<F95Game> f95Games = partition.get(i);
            f95GameMapper.batchUpsert(f95Games);
        }
        List<List<F95GameRelation>> relationPartition = Lists.partition(relations, 2000);
        for (int i = 0; i < relationPartition.size(); i++) {
            LoggerUtil.info(LOGGER, "[f95-game-save](saving relation partition)({}/{})", i + 1, relationPartition.size());
            List<F95GameRelation> f95GameRelations = relationPartition.get(i);
            f95GameRelationMapper.batchUpsert(f95GameRelations);
        }
    }

    public static void main(String[] args) {
        HttpRequest get = HttpUtil.createGet(StrUtil.format("https://f95zone.to/sam/latest_alpha/latest_data.php?cmd=rss&cat=games&page={}&sort=date&rows=90", 1));
        get.setHttpProxy("127.0.0.1", Integer.parseInt("10809"));
        HttpResponse execute = get.execute();
        ValidateUtil.checkTrue(execute.isOk(), "request is not ok: " + execute.getStatus());
        Map<String, Object> stringObjectMap = XmlUtil.xmlToMap(execute.body());
        System.out.println(JSON.toJSONString(stringObjectMap));
    }
}
