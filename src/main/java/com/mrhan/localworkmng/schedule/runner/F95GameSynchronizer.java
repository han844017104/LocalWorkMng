package com.mrhan.localworkmng.schedule.runner;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.mrhan.localworkmng.dal.f95.model.F95Game;
import com.mrhan.localworkmng.dal.f95.model.F95GameRelation;
import com.mrhan.localworkmng.schedule.SchedulerRunner;
import com.mrhan.localworkmng.util.LoggerUtil;
import com.mrhan.localworkmng.util.ValidateUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @Author yuhang
 * @Date 2024-06-08 15:05
 * @Description
 */
@Component
public class F95GameSynchronizer extends SchedulerRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(F95GameSynchronizer.class);

    @Value("spring.net.proxy.host")
    private String proxyHost;

    @Value("spring.net.proxy.port")
    private String proxyPort;

    @Value("biz.f95.sync.limiter")
    private String limiter;

    @Scheduled(cron = "0 0/5 * * * ? ")
    public void doSyncF95Fame() {
        int maxPage = doQueryAndSave(1);
        RateLimiter rateLimiter = RateLimiter.create(Double.parseDouble(StrUtil.blankToDefault(limiter, "5")));
        for (int page = 2; page <= maxPage; page++) {
            int finalPage = page;
            CompletableFuture.runAsync(() -> {
                rateLimiter.acquire();
                boolean s = true;
                long start = System.currentTimeMillis();
                try {
                    doQueryAndSave(finalPage);
                } catch (Exception e) {
                    s = false;
                    LoggerUtil.error(LOGGER, e, "[doSyncF95Fame](sync failed)({})", finalPage);
                } finally {
                    LoggerUtil.info(LOGGER, "[doSyncF95Fame](do page sync finished)({})({},{})", finalPage,
                            s ? "Y" : "N", System.currentTimeMillis() - start);
                }
            }, commonSchedulerThreadPool);
        }

    }

    private Integer doQueryAndSave(int page) {
        HttpRequest get = HttpUtil.createGet(StrUtil.format("https://f95zone.to/sam/latest_alpha/latest_data.php?cmd=list&cat=games&page={}&sort=date&rows=90", page));
        get.setHttpProxy(proxyHost, Integer.parseInt(proxyPort));
        HttpResponse execute = get.execute();
        ValidateUtil.checkTrue(execute.isOk(), "request is not ok: " + execute.getStatus());
        JSONObject body = JSON.parseObject(execute.body());
        LoggerUtil.info(LOGGER, "[doSyncF95Fame](resp body)({})", body);
        ValidateUtil.checkTrue(MapUtils.isNotEmpty(body), "body is empty");
        String string = body.getString("status");
        ValidateUtil.checkTrue("ok".equals(string), "status is not ok: " + string);
        JSONObject pagination = body.getJSONObject("msg").getJSONObject("pagination");

        JSONArray jsonArray = body.getJSONObject("msg").getJSONArray("data");
        batchSave(jsonArray);
        return pagination.getInteger("total");
    }

    private void batchSave(JSONArray jsonArray) {

    }
//
//    private Pair<F95Game, List<F95GameRelation>> transform(JSONObject origin) {
//        F95Game f95Game = new F95Game();
//        f95Game.setThreadId(origin.getString("thread_id"));
//        f95Game.setTitle(origin.getString("title"));
//        f95Game.setGameVersion(origin.getString("version"));
//        f95Game.setViews(origin.getLongValue("views"));
//        f95Game.setLikes(origin.getLongValue("likes"));
//        f95Game.setLikeRatio(f95Game.getViews() == 0L ? BigDecimal.ZERO :
//                new BigDecimal(f95Game.getLikes())
//                        .divide(new BigDecimal(f95Game.getViews()), RoundingMode.HALF_UP)
//                        .setScale(2, RoundingMode.HALF_UP));
//        f95Game.setRating(BigDecimal.valueOf(origin.getDouble("rating")));
//        f95Game.setGameUpdateDate(parseDate());
//        f95Game.setGmtCreate();
//        f95Game.setGmtModified();
//        f95Game.setStatus();
//        f95Game.setExtInfo();
//
//
//    }
//
//    private Date parseDate(String date) {
//
//    }
public static void main(String[] args) {
    HttpRequest get = HttpUtil.createGet(StrUtil.format("https://f95zone.to/sam/latest_alpha/latest_data.php?cmd=rss&cat=games&page={}&sort=date&rows=90", 1));
    get.setHttpProxy("127.0.0.1", Integer.parseInt("10809"));
    HttpResponse execute = get.execute();
    ValidateUtil.checkTrue(execute.isOk(), "request is not ok: " + execute.getStatus());
    Map<String, Object> stringObjectMap = XmlUtil.xmlToMap(execute.body());
    System.out.println(JSON.toJSONString(stringObjectMap));
}
}
