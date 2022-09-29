/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.core.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.response.PageResult;
import lombok.Getter;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author yuhang
 * @Date 2022-09-29 15:19
 * @Description
 */
public class CommonPageTemplate<BO, DO> {

    @Getter
    private final BaseMapper<DO> mapper;

    public CommonPageTemplate(BaseMapper<DO> mapper) {
        this.mapper = mapper;
    }

    public PageResult<BO> queryPage(PageRequest<?> request, Function<DO, BO> d2bConverter,
                                    String... ignoreEntityProperties) {
        return queryPage(request, d2bConverter, false, null, ignoreEntityProperties);
    }

    public PageResult<BO> queryPage(PageRequest<?> request, Function<DO, BO> d2bConverter,
                                    boolean ignoreRequestEntity, QueryWrapper<DO> wrapper,
                                    String... ignoreEntityProperties) {
        Page<DO> doPage = buildPage(request);
        QueryWrapper<DO> queryWrapper = buildWrapper(request, ignoreRequestEntity, wrapper, ignoreEntityProperties);
        Page<DO> selectPage = mapper.selectPage(doPage, queryWrapper);
        return convert(request.isPaged(), selectPage, d2bConverter);
    }

    protected Page<DO> buildPage(PageRequest<?> request) {
        Page<DO> page = new Page<>();
        page.setSize(-1);
        if (request.isPaged()) {
            page.setCurrent(request.getCurrentPage());
            page.setSize(request.getSize());
        }
        return page;
    }

    public QueryWrapper<DO> buildWrapper(PageRequest<?> request, boolean ignoreRequestEntity,
                                         QueryWrapper<DO> wrapper, String... ignoreEntityProperties) {
        QueryWrapper<DO> queryWrapper;
        queryWrapper = Objects.requireNonNullElseGet(wrapper, QueryWrapper::new);
        if (ignoreRequestEntity) {
            return queryWrapper;
        }
        if (request.getCondition() != null) {
            HashSet<String> ignoreEntityPropertiesSet = Optional.ofNullable(ignoreEntityProperties)
                    .map(Sets::newHashSet).orElse(Sets.newHashSet());
            Map<String, Object> paramMap = BeanUtil.beanToMap(request.getCondition(), new LinkedHashMap<>(), true,
                    key -> {
                        if (!ignoreEntityPropertiesSet.contains(key)) {
                            return StrUtil.toUnderlineCase(key);
                        }
                        return null;
                    });
            paramMap.forEach(wrapper::eq);
        }
        buffWrapper(queryWrapper);
        return queryWrapper;
    }

    protected PageResult<BO> convert(boolean isPaged, IPage<DO> page, Function<DO, BO> d2bConverter) {
        PageResult<BO> pageResult = new PageResult<>();
        pageResult.setPaged(isPaged);
        pageResult.setCurrentPage(page.getCurrent());
        pageResult.setSize(page.getSize());
        pageResult.setTotal(page.getTotal());
        pageResult.setSuccess(true);
        List<BO> list = Optional.ofNullable(page.getRecords()).orElse(Lists.newArrayList()).stream()
                .map(d2bConverter).collect(Collectors.toList());
        pageResult.setResults(list);
        return pageResult;
    }


    protected void buffWrapper(QueryWrapper<DO> wrapper) {
        // do nothing, sub templates could override this method to do something default
    }
}
