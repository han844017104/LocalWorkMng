/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.core.trans.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mrhan.localworkmng.core.template.CommonPageTemplate;
import com.mrhan.localworkmng.core.trans.TranslatedTextService;
import com.mrhan.localworkmng.dal.trans.mapper.TranslatedTextMapper;
import com.mrhan.localworkmng.dal.trans.model.TranslatedTextDO;
import com.mrhan.localworkmng.model.bo.TranslatedTextBO;
import com.mrhan.localworkmng.model.enums.ResultCode;
import com.mrhan.localworkmng.model.enums.TranslateEngineEnum;
import com.mrhan.localworkmng.model.exception.BizException;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.request.trans.TransTextQueryParam;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.util.BeanUtil;
import com.mrhan.localworkmng.util.CryptUtil;
import com.mrhan.localworkmng.util.ValidateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author yuhang
 * @Date 2022-08-22 11:04
 * @Description
 */
@Service
public class TranslatedTextServiceImpl implements TranslatedTextService {

    private static final Splitter SPLITTER = Splitter.on("#").omitEmptyStrings();

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
                .in(CollectionUtils.isNotEmpty(condition.getEngines()), "trans_engine", condition.getEngines())
                .in(CollectionUtils.isNotEmpty(condition.getOriginalDigestList()), "original_digest",
                        condition.getOriginalDigestList());
        if (StrUtil.isNotBlank(condition.getFuzzyStr())) {
            wrapper.and(wp ->
                    wp.like("text_original", condition.getFuzzyStr())
                            .or()
                            .like("text_trans", condition.getFuzzyStr()));
        }
        if (CollectionUtils.isNotEmpty(condition.getOriginalDigestAndFromList())) {
            condition.getOriginalDigestAndFromList().forEach(e -> {
                List<String> strings = SPLITTER.splitToList(e);
                String original = strings.get(0);
                String from = strings.get(1);
                wrapper.or(w -> {
                    w.eq("original_digest", original)
                            .eq("from_language", from);
                });
            });
        }
        wrapper.orderByDesc("id");
        return template.queryPage(request, this::convertD2B, true, wrapper);
    }

    @Override
    public boolean updateTransText(TranslatedTextBO bo) {
        TranslatedTextDO exist = translatedTextMapper.selectById(bo.getId());
        ValidateUtil.checkNotNull(exist, "数据不存在");
        exist.setTransDigest(CryptUtil.digestMd5(bo.getTextTrans()));
        exist.setTextTrans(bo.getTextTrans());
        return translatedTextMapper.updateById(exist) > 0;
    }

    @Override
    @Transactional(transactionManager = "transTransactionManager", rollbackFor = Exception.class)
    public TranslatedTextBO upsertTransText(TranslatedTextBO bo, boolean override) {
        PageRequest<TransTextQueryParam> request = new PageRequest<TransTextQueryParam>()
                .withCondition(new TransTextQueryParam())
                .condWrap(e -> {
                    e.setFrom(bo.getFromLanguage());
                    e.setTo(bo.getToLanguage());
                    e.setOrigin(bo.getTextOriginal());
                    e.setOriginalDigestList(Lists.newArrayList(bo.getOriginalDigest()));
                    e.setEngines(Lists.newArrayList(TranslateEngineEnum.CUSTOM.name()));
                });
        PageResult<TranslatedTextBO> exist = query(request);
        if (CollectionUtils.isNotEmpty(exist.getResults())) {
            ValidateUtil.checkTrue(exist.getResults().size() == 1, "too many exist data: " + exist.getResults().size());
            TranslatedTextBO existData = exist.getResults().get(0);
            if (!override) {
                return existData;
            }
            existData.setTextTrans(bo.getTextTrans());
            existData.setTransDigest(bo.getTransDigest());
            translatedTextMapper.updateById(existData);
            return null;
        }

        translatedTextMapper.add(bo);
        return null;
    }

    private TranslatedTextBO convertD2B(TranslatedTextDO ado) {
        return BeanUtil.copy(ado, TranslatedTextBO.class);
    }
}
