/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.core.trans.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Splitter;
import com.mrhan.localworkmng.core.template.CommonPageTemplate;
import com.mrhan.localworkmng.core.trans.TranslatedTextService;
import com.mrhan.localworkmng.dal.trans.mapper.TranslatedTextMapper;
import com.mrhan.localworkmng.dal.trans.model.TranslatedTextDO;
import com.mrhan.localworkmng.model.bo.TranslatedTextBO;
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
            wrapper.and(wp -> condition.getOriginalDigestAndFromList().forEach(e -> {
                List<String> strings = SPLITTER.splitToList(e);
                String original = strings.get(0);
                String from = strings.get(1);
                wrapper.or(w -> {
                    w.eq("original_digest", original)
                            .eq("from_language", from);
                });
            }));
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
    public Long insertTransText(TranslatedTextBO bo) {
        translatedTextMapper.add(bo);
        return bo.getId();
    }

    private TranslatedTextBO convertD2B(TranslatedTextDO ado) {
        return BeanUtil.copy(ado, TranslatedTextBO.class);
    }
}
