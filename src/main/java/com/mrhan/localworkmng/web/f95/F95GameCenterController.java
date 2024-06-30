package com.mrhan.localworkmng.web.f95;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Lists;
import com.mrhan.localworkmng.core.f95.F95GamePrefixService;
import com.mrhan.localworkmng.core.f95.F95GameSearchService;
import com.mrhan.localworkmng.core.f95.F95GameService;
import com.mrhan.localworkmng.core.f95.F95GameTagService;
import com.mrhan.localworkmng.dal.f95.model.F95Game;
import com.mrhan.localworkmng.dal.f95.model.F95GamePrefix;
import com.mrhan.localworkmng.dal.f95.model.F95GameTag;
import com.mrhan.localworkmng.integration.transcore.TranslateCoreClient;
import com.mrhan.localworkmng.model.bo.F95GameFatInfo;
import com.mrhan.localworkmng.model.enums.F95PrefixTypeEnum;
import com.mrhan.localworkmng.model.request.CommonSortOrder;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.request.f95.*;
import com.mrhan.localworkmng.model.response.BaseResult;
import com.mrhan.localworkmng.model.response.ListResult;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.model.response.f95.F95GamePrefixViewInfo;
import com.mrhan.localworkmng.model.response.f95.F95GameTagViewInfo;
import com.mrhan.localworkmng.model.response.f95.F95GameViewInfo;
import com.mrhan.localworkmng.util.BeanUtil;
import com.mrhan.localworkmng.util.JsonUtil;
import com.mrhan.localworkmng.util.PageModelUtil;
import com.mrhan.localworkmng.util.ValidateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author yuhang
 * @Date 2024-06-08 13:30
 * @Description
 */
@RestController
@RequestMapping("/f95")
@Api(value = "F95游戏仓Controller", tags = {"F95"})
public class F95GameCenterController {

    @Resource
    private F95GameSearchService f95GameSearchService;

    @Resource
    private F95GameService f95GameService;

    @Resource
    private F95GameTagService f95GameTagService;

    @Resource
    private F95GamePrefixService f95GamePrefixService;

    @Resource
    private TranslateCoreClient translateCoreClient;

    @PostMapping("/queryGame")
    @ApiOperation("游戏查询")
    public PageResult<F95GameFatInfo> query(@RequestBody PageRequest<F95GameSearchParam> request) {
        ValidateUtil.checkNotNull(request, "入参为空");
        if (request.getCondition() == null) {
            request.setCondition(new F95GameSearchParam());
        }
        return f95GameSearchService.query(request);
    }

    @PostMapping("/queryGameForView")
    @ApiOperation("游戏视图查询")
    public PageResult<F95GameViewInfo> queryForView(@RequestBody PageRequest<F95GameSearchParam> request) {
        ValidateUtil.checkNotNull(request, "入参为空");
        if (request.getCondition() == null) {
            request.setCondition(new F95GameSearchParam());
        }
        if (CollectionUtils.isEmpty(request.getCondition().getOrders())) {
            request.getCondition().setOrders(List.of(new CommonSortOrder("gameUpdateDate", false)));
        }
        initItemCondition(request.getCondition());
        PageResult<F95GameFatInfo> result = f95GameSearchService.query(request);
        return PageModelUtil.transformResult(result, this::transformGame2ViewInfo);
    }

    @PostMapping("/registerConfiguration")
    @ApiOperation("注册F95基础设定")
    public BaseResult registerF95BaseConfiguration(@RequestBody F95BaseConfigParam param) {
        ValidateUtil.checkNotNull(param, "req is null");
        ValidateUtil.checkNotBlank(param.getConfigStr(), "config str is blank");
        JSONObject config = JsonUtil.nonNullTryParse(param.getConfigStr());
        JSONObject tags = config.getJSONObject("tags");
        JSONObject prefixes = config.getJSONObject("prefixes");
        JSONArray gamePrefixes = prefixes.getJSONArray("games");
        ValidateUtil.checkTrue(MapUtils.isNotEmpty(tags), "empty tags");
        ValidateUtil.checkTrue(CollectionUtils.isNotEmpty(gamePrefixes), "empty gamePrefixes");
        registerTags(tags);
        registerPrefixes(gamePrefixes);
        return new BaseResult(true);
    }

    private void registerTags(JSONObject tags) {
        List<F95GameTag> list = Lists.newLinkedList();
        for (String tagId : tags.keySet()) {
            String enName = tags.getString(tagId);
            if (enName.startsWith("asset-")) {
                continue;
            }
            list.add(new F95GameTag(null, tagId, tags.getString(tagId), translateCoreClient.translate(enName, "en", "zh")));
        }
        f95GameTagService.batchUpsert(list);
    }


    private void registerPrefixes(JSONArray gamePrefixes) {
        for (int i = 0; i < gamePrefixes.size(); i++) {
            JSONObject one = gamePrefixes.getJSONObject(i);
            String name = one.getString("name");
            JSONArray prefixes = one.getJSONArray("prefixes");
            String type;
            if (StringUtils.equalsIgnoreCase(name, "Engine")) {
                type = F95PrefixTypeEnum.ENGINE.name();
            } else if (StringUtils.equalsIgnoreCase(name, "Status")) {
                type = F95PrefixTypeEnum.STATUS.name();
            } else {
                type = F95PrefixTypeEnum.PREFIX.name();
            }
            List<F95GamePrefix> list = Lists.newLinkedList();
            for (int i1 = 0; i1 < prefixes.size(); i1++) {
                JSONObject e = prefixes.getJSONObject(i1);
                list.add(new F95GamePrefix(null, e.getString("id"), e.getString("name"),
                        translateCoreClient.translate(e.getString("name"), "en", "zh"),
                        type));
            }
            f95GamePrefixService.batchUpsert(list);
        }
    }

    private F95GameViewInfo transformGame2ViewInfo(F95GameFatInfo fatInfo) {
        return BeanUtil.copy(fatInfo, F95GameViewInfo.class, view -> {
            view.setLikeRatio(BigDecimal.valueOf(fatInfo.getLikeRatio()));
            view.setRating(BigDecimal.valueOf(fatInfo.getRating()));
            view.setGameUpdateDate(new Date(fatInfo.getGameUpdateDate()));
            view.setGmtCreate(new Date(fatInfo.getGmtCreate()));
            view.setGmtModified(new Date(fatInfo.getGmtModified()));
            F95Game f95Game = f95GameService.queryByTid(fatInfo.getThreadId());
            if (f95Game != null) {
                view.setChTitle(f95Game.getChTitle());
                JSONObject ext = JsonUtil.nonNullTryParse(f95Game.getExtInfo());
                JSONObject originData = ext.getJSONObject("originData");
                if (MapUtils.isNotEmpty(originData)) {
                    view.setMainPreview(originData.getString("cover"));
                    view.setPreviews(originData.getObject("screens", new TypeReference<>() {
                    }));
                }
            }
            List<F95GameTag> f95GameTags = f95GameTagService.queryByTagIdList(fatInfo.getTagIds());
            List<F95GamePrefix> f95GamePrefixes = f95GamePrefixService.queryByPidList(fatInfo.getPrefixIds());
            view.setTags(f95GameTags.stream().map(e -> BeanUtil.copy(e, F95GameTagViewInfo.class)).filter(Objects::nonNull)
                    .sorted(Comparator.comparing(F95GameTagViewInfo::getTagEnName)).collect(Collectors.toList()));
            List<F95GamePrefixViewInfo> prefix = Lists.newArrayList();
            List<F95GamePrefixViewInfo> status = Lists.newArrayList();
            List<F95GamePrefixViewInfo> engine = Lists.newArrayList();
            for (F95GamePrefix gamePrefix : f95GamePrefixes) {
                if (StringUtils.equalsIgnoreCase(gamePrefix.getPrefixType(), F95PrefixTypeEnum.ENGINE.name())) {
                    engine.add(BeanUtil.copy(gamePrefix, F95GamePrefixViewInfo.class));
                } else if (StringUtils.equalsIgnoreCase(gamePrefix.getPrefixType(), F95PrefixTypeEnum.STATUS.name())) {
                    status.add(BeanUtil.copy(gamePrefix, F95GamePrefixViewInfo.class));
                } else {
                    prefix.add(BeanUtil.copy(gamePrefix, F95GamePrefixViewInfo.class));
                }
            }
            view.setPrefixes(prefix.stream().sorted(Comparator.comparing(F95GamePrefixViewInfo::getPrefixEnName)).collect(Collectors.toList()));
            view.setGameEngine(engine.stream().sorted(Comparator.comparing(F95GamePrefixViewInfo::getPrefixEnName)).collect(Collectors.toList()));
            view.setGameStatus(status.stream().sorted(Comparator.comparing(F95GamePrefixViewInfo::getPrefixEnName)).collect(Collectors.toList()));
        });
    }

    private void initItemCondition(F95GameSearchParam param) {
        if (CollectionUtils.isEmpty(param.getItemConditions())) {
            param.setItemConditions(Lists.newArrayList());
        }
        if (CollectionUtils.isNotEmpty(param.getGameStatusConditions())) {
            for (SimpleItemCondition condition : param.getGameStatusConditions()) {
                if (StrUtil.isBlank(condition.getId())) {
                    continue;
                }
                param.getItemConditions().add(new ItemCondition("prefixIds", List.of(condition.getId()), condition.isEq() ? "=" : "!="));
            }
        }
        if (CollectionUtils.isNotEmpty(param.getGameEngineConditions())) {
            for (SimpleItemCondition condition : param.getGameEngineConditions()) {
                if (StrUtil.isBlank(condition.getId())) {
                    continue;
                }
                param.getItemConditions().add(new ItemCondition("prefixIds", List.of(condition.getId()), condition.isEq() ? "=" : "!="));
            }
        }
        if (CollectionUtils.isNotEmpty(param.getGamePrefixConditions())) {
            for (SimpleItemCondition condition : param.getGamePrefixConditions()) {
                if (StrUtil.isBlank(condition.getId())) {
                    continue;
                }
                param.getItemConditions().add(new ItemCondition("prefixIds", List.of(condition.getId()), condition.isEq() ? "=" : "!="));
            }
        }
        if (CollectionUtils.isNotEmpty(param.getGameTagConditions())) {
            for (SimpleBatchItemCondition condition : param.getGameTagConditions()) {
                if (CollectionUtils.isEmpty(condition.getIds())) {
                    continue;
                }
                param.getItemConditions().add(new ItemCondition("tagIds", condition.getIds(), condition.isEq() ? "=" : "!="));
            }
        }

    }

    @PostMapping("/queryAllTags")
    @ApiOperation("查询所有标签")
    public ListResult<F95GameTagViewInfo> queryAllTags() {
        return new ListResult<>(f95GameTagService.queryAll()
                .stream().map(e -> BeanUtil.copy(e, F95GameTagViewInfo.class)).collect(Collectors.toList()));
    }

    @PostMapping("/queryAllPrefixes")
    @ApiOperation("查询所有普通前缀")
    public ListResult<F95GamePrefixViewInfo> queryAllPrefixes() {
        return new ListResult<>(f95GamePrefixService.queryByType(F95PrefixTypeEnum.PREFIX.name())
                .stream().map(e -> BeanUtil.copy(e, F95GamePrefixViewInfo.class)).collect(Collectors.toList()));
    }

    @PostMapping("/queryAllStatusPrefixes")
    @ApiOperation("查询所有游戏状态前缀")
    public ListResult<F95GamePrefixViewInfo> queryAllStatusPrefixes() {
        return new ListResult<>(f95GamePrefixService.queryByType(F95PrefixTypeEnum.STATUS.name())
                .stream().map(e -> BeanUtil.copy(e, F95GamePrefixViewInfo.class)).collect(Collectors.toList()));
    }

    @PostMapping("/queryAllEnginePrefixes")
    @ApiOperation("查询所有引擎前缀")
    public ListResult<F95GamePrefixViewInfo> queryAllEnginePrefixes() {
        return new ListResult<>(f95GamePrefixService.queryByType(F95PrefixTypeEnum.ENGINE.name())
                .stream().map(e -> BeanUtil.copy(e, F95GamePrefixViewInfo.class)).collect(Collectors.toList()));
    }

}
