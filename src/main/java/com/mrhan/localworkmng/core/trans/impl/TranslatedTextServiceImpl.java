/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.core.trans.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrhan.localworkmng.core.trans.TranslatedTextService;
import com.mrhan.localworkmng.dal.trans.mapper.TranslatedTextMapper;
import com.mrhan.localworkmng.dal.trans.model.TranslatedTextDO;
import com.mrhan.localworkmng.model.bo.TranslatedTextBO;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.request.trans.TransTextQueryParam;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.util.BeanUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yuhang
 * @Date 2022-08-22 11:04
 * @Description
 */
@Service
public class TranslatedTextServiceImpl implements TranslatedTextService {

    @Resource
    private TranslatedTextMapper translatedTextMapper;

    @Override
    public PageResult<TranslatedTextBO> query(PageRequest<TransTextQueryParam> request) {
        PageResult<TranslatedTextBO> result = new PageResult<>();
        List<TranslatedTextBO> list = translatedTextMapper.selectList(
                new QueryWrapper<TranslatedTextDO>().lambda()
                        .orderByDesc(TranslatedTextDO::getId)
                        .last(" limit 1")
        ).stream().map(this::convertD2B).collect(Collectors.toList());
        result.setResults(list);
        return result;
    }

    private TranslatedTextBO convertD2B(TranslatedTextDO ado) {
        return BeanUtil.copy(ado, TranslatedTextBO.class);
    }
}
