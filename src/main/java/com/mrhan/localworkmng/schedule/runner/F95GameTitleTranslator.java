package com.mrhan.localworkmng.schedule.runner;

import com.alibaba.fastjson2.JSONObject;
import com.mrhan.localworkmng.core.template.CommonPageTemplate;
import com.mrhan.localworkmng.dal.f95.mapper.F95GameMapper;
import com.mrhan.localworkmng.dal.f95.model.F95Game;
import com.mrhan.localworkmng.integration.transcore.TranslateCoreClient;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.util.JsonUtil;
import com.mrhan.localworkmng.util.LoggerUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author yuhang
 * @Date 2024-06-30 13:29
 * @Description
 */
@Component
public class F95GameTitleTranslator {

    private static final Logger LOGGER = LoggerFactory.getLogger(F95GameTitleTranslator.class);

    @Resource
    private F95GameMapper f95GameMapper;

    @Resource
    private TranslateCoreClient translateCoreClient;

    @Scheduled(cron = "0 0/6 * * * ? ")
    public void translate() {
        List<F95Game> games = loadAllGames();
        LoggerUtil.info(LOGGER, "[F95GameTitleTranslator](all game loaded)({})", games.size());
        List<F95Game> noChGames = games.stream().filter(e -> StringUtils.isBlank(e.getChTitle())).toList();
        LoggerUtil.info(LOGGER, "[F95GameTitleTranslator](filtered no ch title games)({})", noChGames.size());
        int i = 0;
        for (F95Game game : noChGames) {
            JSONObject ext = JsonUtil.nonNullTryParse(game.getExtInfo());
            String gameChTitle = ext.getString("gameChTitle");
            if (StringUtils.isNotBlank(gameChTitle)) {
                game.setChTitle(gameChTitle);
            } else {
                game.setChTitle(translateCoreClient.translate(game.getTitle(), "en", "zh"));
            }
            f95GameMapper.updateChTitle(game);
            i++;
            LoggerUtil.info(LOGGER, "[F95GameTitleTranslator](translated)({}/{})", i, noChGames.size());
        }
    }

    private List<F95Game> loadAllGames() {
        CommonPageTemplate<F95Game, F95Game> template = new CommonPageTemplate<>(
                f95GameMapper);
        PageRequest<F95Game> request = new PageRequest<>();
        request.setPaged(true);
        request.setCurrentPage(1);
        request.setSize(5000L);
        List<F95Game> games = new ArrayList<>();
        while (true) {
            PageResult<F95Game> pageResult = template.queryPage(request, e -> e, true, null);
            if (CollectionUtils.isEmpty(pageResult.getResults())) {
                LoggerUtil.info(LOGGER, "[F95GameTitleTranslator][loadAllGames](load game empty)({})", request.getCurrentPage());
                break;
            }
            LoggerUtil.info(LOGGER, "[F95GameTitleTranslator][loadAllGames](load game)({})({})", request.getCurrentPage(), pageResult.getResults().size());
            games.addAll(pageResult.getResults());
            request.setCurrentPage(request.getCurrentPage() + 1L);
        }
        LoggerUtil.info(LOGGER, "[F95GameTitleTranslator][loadAllGames](load game finish)({})", games.size());
        return games;
    }
}
