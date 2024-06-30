package com.mrhan.localworkmng.core.f95;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResultPaginated;
import com.meilisearch.sdk.model.Settings;
import com.meilisearch.sdk.model.TaskInfo;
import com.mrhan.localworkmng.core.template.CommonPageTemplate;
import com.mrhan.localworkmng.dal.f95.mapper.F95GameMapper;
import com.mrhan.localworkmng.dal.f95.mapper.F95GamePrefixMapper;
import com.mrhan.localworkmng.dal.f95.mapper.F95GameRelationMapper;
import com.mrhan.localworkmng.dal.f95.mapper.F95GameTagMapper;
import com.mrhan.localworkmng.dal.f95.model.F95Game;
import com.mrhan.localworkmng.dal.f95.model.F95GamePrefix;
import com.mrhan.localworkmng.dal.f95.model.F95GameRelation;
import com.mrhan.localworkmng.dal.f95.model.F95GameTag;
import com.mrhan.localworkmng.integration.meili.MeiLiSearchClient;
import com.mrhan.localworkmng.model.bo.F95GameFatInfo;
import com.mrhan.localworkmng.model.bo.F95GamePrefixInfo;
import com.mrhan.localworkmng.model.bo.F95GameTagInfo;
import com.mrhan.localworkmng.model.enums.F95RelationTypeEnum;
import com.mrhan.localworkmng.model.request.CommonSortOrder;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.request.f95.F95GameSearchParam;
import com.mrhan.localworkmng.model.request.f95.ItemCondition;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.util.BeanUtil;
import com.mrhan.localworkmng.util.LoggerUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author yuhang
 * @Date 2024-06-04 21:57
 * @Description
 */
@Service
public class F95GameSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(F95GameSearchService.class);

    private static final String F95_NAME_SPACE = "f95game";

    @Resource
    private F95GameMapper f95GameMapper;

    @Resource
    private F95GamePrefixMapper f95GamePrefixMapper;

    @Resource
    private F95GameRelationMapper f95GameRelationMapper;

    @Resource
    private F95GameTagMapper f95GameTagMapper;

    @Resource
    private MeiLiSearchClient meiLiSearchClient;

    @PostConstruct
    public void init() {
        meiLiSearchClient.getClient().deleteIndex(F95_NAME_SPACE);
        TaskInfo taskInfo = meiLiSearchClient.getClient().createIndex(F95_NAME_SPACE, "threadId");
        meiLiSearchClient.getClient().waitForTask(taskInfo.getTaskUid());
        Index index = meiLiSearchClient.index(F95_NAME_SPACE);
        Settings settings = index.getSettings();
        initSettings(settings);
        index.updateSettings(settings);

        refresh();
    }

    @Scheduled(fixedDelay = 1000L * 30L)
    public void refresh() {
        List<F95Game> f95Games = loadAllGames();
        List<F95GameFatInfo> f95GameFatInfos = fillAndTransform2FatInfos(f95Games);
        Index index = meiLiSearchClient.index(F95_NAME_SPACE);
        index.addDocuments(JSON.toJSONString(f95GameFatInfos));
    }

    public PageResult<F95GameFatInfo> query(PageRequest<F95GameSearchParam> request) {
        F95GameSearchParam param = request.getCondition();
        Index index = meiLiSearchClient.index(F95_NAME_SPACE);
        SearchRequest.SearchRequestBuilder builder = SearchRequest.builder();
        builder.page(Long.valueOf(request.getCurrentPage()).intValue());
        builder.hitsPerPage(Long.valueOf(request.getSize()).intValue());
        if (StrUtil.isNotBlank(param.getFuzzy())) {
            builder.q(param.getFuzzy());
        }
        List<List<String>> filter = new ArrayList<>();
        List<String> sort = new ArrayList<>();
        if (StrUtil.isNotBlank(param.getThreadId())) {
            filter.add(Lists.newArrayList("threadId = " + param.getThreadId()));
        }
        if (param.getMinView() != null) {
            filter.add(Lists.newArrayList("views >= " + param.getMinView()));
        }
        if (param.getMaxView() != null) {
            filter.add(Lists.newArrayList("views <= " + param.getMaxView()));
        }
        if (param.getMinLike() != null) {
            filter.add(Lists.newArrayList("likes >= " + param.getMinLike()));
        }
        if (param.getMaxLike() != null) {
            filter.add(Lists.newArrayList("likes <= " + param.getMaxLike()));
        }
        if (param.getMinLikeRatio() != null) {
            filter.add(Lists.newArrayList("likeRatio >= " + param.getMinLikeRatio().doubleValue()));
        }
        if (param.getMaxLikeRatio() != null) {
            filter.add(Lists.newArrayList("likeRatio <= " + param.getMaxLikeRatio().doubleValue()));
        }
        if (param.getMinRating() != null) {
            filter.add(Lists.newArrayList("rating >= " + param.getMinRating().doubleValue()));
        }
        if (param.getMaxRating() != null) {
            filter.add(Lists.newArrayList("rating <= " + param.getMaxRating().doubleValue()));
        }
        if (param.getMinGameUpdateDate() != null) {
            filter.add(Lists.newArrayList("gameUpdateDate >= " + param.getMinGameUpdateDate()));
        }
        if (param.getMaxGameUpdateDate() != null) {
            filter.add(Lists.newArrayList("gameUpdateDate <= " + param.getMaxGameUpdateDate()));
        }
        if (CollectionUtils.isNotEmpty(param.getItemConditions())) {
            for (ItemCondition condition : param.getItemConditions()) {
                List<String> ones = Lists.newArrayList();
                for (String value : condition.getValues()) {
                    ones.add(condition.getType() + " " + condition.getOp() + " " + value);
                }
                filter.add(ones);
            }
        }
        if (CollectionUtils.isNotEmpty(param.getOrders())) {
            for (CommonSortOrder order : param.getOrders()) {
                sort.add(order.getColumn() + ":" + (order.isAsc() ? "asc" : "desc"));
            }
        }
        builder.filterArray(transFilters(filter));
        builder.sort(sort.toArray(new String[]{}));

        SearchResultPaginated search = (SearchResultPaginated) index.search(builder.build());
        List<F95GameFatInfo> games = JSON.parseArray(JSON.toJSONString(search.getHits()), F95GameFatInfo.class);
        PageResult<F95GameFatInfo> pageResult = new PageResult<>();
        pageResult.setPaged(true);
        pageResult.setCurrentPage(request.getCurrentPage());
        pageResult.setSize(request.getSize());
        pageResult.setTotal(search.getTotalHits());
        pageResult.setSuccess(true);
        pageResult.setResults(games);
        return pageResult;
    }

    private String[][] transFilters(List<List<String>> filter) {
        if (CollectionUtils.isEmpty(filter)) {
            return new String[][]{};
        }
        String[][] array = new String[filter.size()][];
        for (int i = 0; i < filter.size(); i++) {
            List<String> strings = filter.get(i);
            String[] items;
            if (CollectionUtils.isEmpty(strings)) {
                items = new String[0];
            } else {
                items = strings.toArray(new String[0]);
            }
            array[i] = items;
        }
        return array;
    }

    private void initSettings(Settings settings) {
        settings.setRankingRules(
                new String[]{
                        "words",
                        "typo",
                        "proximity",
                        "attribute",
                        "sort",
                        "exactness",
                        "release_date:desc",
                        "rank:desc"
                });
        settings.setDistinctAttribute("threadId");
        settings.setStopWords(
                new String[]{
                        "the",
                        "a",
                        "an"
                });
        settings.setSortableAttributes(
                new String[]{
                        "views",
                        "likeRatio",
                        "rating",
                        "gameUpdateDate",
                        "likes"
                });
        settings.setFilterableAttributes(new String[]{
                "threadId",
                "views",
                "likes",
                "likeRatio",
                "rating",
                "gameUpdateDate",
                "tagIds",
                "prefixIds"
        });
        settings.getPagination().setMaxTotalHits(99999);
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
                LoggerUtil.info(LOGGER, "[f95Game][loadAllGames](load game empty)({})", request.getCurrentPage());
                break;
            }
            LoggerUtil.info(LOGGER, "[f95Game][loadAllGames](load game)({})({})", request.getCurrentPage(), pageResult.getResults().size());
            games.addAll(pageResult.getResults());
            request.setCurrentPage(request.getCurrentPage() + 1L);
        }
        LoggerUtil.info(LOGGER, "[f95Game][loadAllGames](load game finish)({})", games.size());
        return games;
    }

    private List<F95GameFatInfo> fillAndTransform2FatInfos(List<F95Game> games) {
        if (CollectionUtils.isEmpty(games)) {
            return new ArrayList<>();
        }
        Map<String, F95GameTag> tagContainer = f95GameTagMapper.selectList(Wrappers.lambdaQuery()).stream().collect(Collectors.toMap(F95GameTag::getTagId, e -> e));
        Map<String, F95GamePrefix> prefixContainer = f95GamePrefixMapper.selectList(Wrappers.lambdaQuery()).stream().collect(Collectors.toMap(F95GamePrefix::getPrefixId, e -> e));
        List<List<F95Game>> partition = Lists.partition(games, 500);
        Map<String, GameExtends> extendsMap = new ConcurrentHashMap<>();
        partition.stream().parallel().forEach(list -> {
            List<String> tidList = list.stream().map(F95Game::getThreadId).collect(Collectors.toList());
            List<F95GameRelation> relations = f95GameRelationMapper.selectList(
                    Wrappers.<F95GameRelation>lambdaQuery()
                            .in(F95GameRelation::getTid, tidList)
            );
            Map<String, List<F95GameRelation>> tid2RelationMapping = relations.stream().collect(Collectors.groupingBy(F95GameRelation::getTid));
            tid2RelationMapping.forEach((tid, relationList) -> {
                List<F95GameTag> tagList = new ArrayList<>();
                List<F95GamePrefix> prefixList = new ArrayList<>();
                relationList.forEach(one -> {
                    F95RelationTypeEnum typeEnum = F95RelationTypeEnum.fromCode(one.getRelationType());
                    if (typeEnum == null) {
                        return;
                    }
                    switch (typeEnum) {
                        case TAG -> tagList.add(tagContainer.get(one.getOutId()));
                        case PREFIX -> prefixList.add(prefixContainer.get(one.getOutId()));
                    }
                });
                extendsMap.put(tid, new GameExtends(tagList, prefixList));
            });
        });
        return games.stream().map(game -> BeanUtil.copy(game, F95GameFatInfo.class, copy -> {
            GameExtends gameExtends = extendsMap.get(game.getThreadId());
            if (gameExtends == null) {
                return;
            }
            copy.setTagIds(
                    Optional.ofNullable(gameExtends.getTagList())
                            .map(l ->
                                    l.stream().map(tag -> BeanUtil.copy(tag, F95GameTagInfo.class)).filter(Objects::nonNull).collect(Collectors.toList())
                            )
                            .orElse(new ArrayList<>())
                            .stream().map(F95GameTagInfo::getTagId).collect(Collectors.toList())
            );
            copy.setPrefixIds(
                    Optional.ofNullable(gameExtends.getPrefixList())
                            .map(l ->
                                    l.stream().map(tag -> BeanUtil.copy(tag, F95GamePrefixInfo.class)).filter(Objects::nonNull).collect(Collectors.toList())
                            )
                            .orElse(new ArrayList<>())
                            .stream().map(F95GamePrefixInfo::getPrefixId).collect(Collectors.toList())
            );
            copy.setLikeRatio(Optional.ofNullable(game.getLikeRatio()).map(BigDecimal::doubleValue).orElse(0.0));
            copy.setRating(Optional.ofNullable(game.getRating()).map(BigDecimal::doubleValue).orElse(0.0));
            copy.setGameUpdateDate(game.getGameUpdateDate().getTime());
            copy.setGmtCreate(game.getGmtCreate().getTime());
            copy.setGmtModified(game.getGmtModified().getTime());
        })).filter(Objects::nonNull).collect(Collectors.toList());


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GameExtends {

        private List<F95GameTag> tagList;

        private List<F95GamePrefix> prefixList;
    }

}
