/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.core.trans.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrhan.localworkmng.core.template.CommonPageTemplate;
import com.mrhan.localworkmng.core.trans.TranslatedTextService;
import com.mrhan.localworkmng.dal.trans.mapper.TranslatedTextMapper;
import com.mrhan.localworkmng.dal.trans.model.TranslatedTextDO;
import com.mrhan.localworkmng.model.bo.TranslatedTextBO;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.request.trans.TransTextQueryParam;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.util.BeanUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
        TransTextQueryParam condition = request.getCondition();
        CommonPageTemplate<TranslatedTextBO, TranslatedTextDO> template = new CommonPageTemplate<>(
                translatedTextMapper);
        QueryWrapper<TranslatedTextDO> wrapper = new QueryWrapper<TranslatedTextDO>()
                .eq(StrUtil.isNotBlank(condition.getFrom()), "from_language", condition.getFrom())
                .eq(StrUtil.isNotBlank(condition.getOrigin()), "text_original", condition.getOrigin())
                .eq(StrUtil.isNotBlank(condition.getTo()), "to_language", condition.getTo())
                .eq(StrUtil.isNotBlank(condition.getTrans()), "text_trans", condition.getTrans())
                .in(CollectionUtils.isNotEmpty(condition.getEngines()), "trans_engine", condition.getEngines());
        if (StrUtil.isNotBlank(condition.getFuzzyStr())) {
            wrapper.and(wp ->
                    wp.like("text_original", condition.getFuzzyStr())
                            .or()
                            .like("text_trans", condition.getFuzzyStr()));
        }
        wrapper.orderByDesc("id");
        return template.queryPage(request, this::convertD2B, true, wrapper);
    }

    private TranslatedTextBO convertD2B(TranslatedTextDO ado) {
        return BeanUtil.copy(ado, TranslatedTextBO.class);
    }
}
