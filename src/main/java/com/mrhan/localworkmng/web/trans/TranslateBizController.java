/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.web.trans;

import com.google.common.base.Joiner;
import com.mrhan.localworkmng.core.trans.TranslateLogService;
import com.mrhan.localworkmng.core.trans.TranslatedTextService;
import com.mrhan.localworkmng.model.bo.TranslateFrequentWord;
import com.mrhan.localworkmng.model.bo.TranslatedTextBO;
import com.mrhan.localworkmng.model.enums.TranslateEngineEnum;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.request.trans.TransTextQueryParam;
import com.mrhan.localworkmng.model.response.BaseResult;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.model.response.SimpleResult;
import com.mrhan.localworkmng.model.response.trans.TransLogGroup;
import com.mrhan.localworkmng.model.response.trans.TransTextDTO;
import com.mrhan.localworkmng.util.BeanUtil;
import com.mrhan.localworkmng.util.CryptUtil;
import com.mrhan.localworkmng.util.PageModelUtil;
import com.mrhan.localworkmng.util.ValidateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author yuhang
 * @Date 2022-08-22 11:05
 * @Description
 */
@RestController
@RequestMapping("/trans")
public class TranslateBizController {

    private static final Joiner JOINER = Joiner.on("#").useForNull("-");

    @Resource
    private TranslatedTextService translatedTextService;

    @Resource
    private TranslateLogService translateLogService;

    @PostMapping("/queryTransText")
    public PageResult<TransTextDTO> query(@RequestBody PageRequest<TransTextQueryParam> request) {
        ValidateUtil.checkNotNull(request, "入参为空");
        if (request.getCondition() == null) {
            request.setCondition(new TransTextQueryParam());
        }
        PageResult<TranslatedTextBO> result = translatedTextService.query(request);
        return PageModelUtil.transformResult(result, this::convertTextB2DT);
    }

    @PostMapping("/queryFrequentWords")
    public PageResult<TransLogGroup> queryFrequentWords(@RequestBody PageRequest<TransLogGroup> request) {
        PageResult<TranslateFrequentWord> translateFrequentWordPageResult = translateLogService.queryFrequentWords(
                request);
        List<TranslateFrequentWord> results = translateFrequentWordPageResult.getResults();
        if (CollectionUtils.isNotEmpty(results)) {
            PageRequest<TransTextQueryParam> textQueryRequest = new PageRequest<TransTextQueryParam>()
                    .withCondition(new TransTextQueryParam())
                    .condWrap(e -> e.setOriginalDigestAndFromList(
                            results.stream().map(t -> JOINER.join(t.getOriginalDigest(), t.getFromLanguage()))
                                    .collect(Collectors.toList())
                    ));
            List<TranslatedTextBO> texts = translatedTextService.query(textQueryRequest).getResults();
            Map<String, List<TranslatedTextBO>> originDigestMapping = texts.stream().collect(
                    Collectors.groupingBy(t -> JOINER.join(t.getOriginalDigest(), t.getFromLanguage())));
            return PageModelUtil.transformResult(translateFrequentWordPageResult,
                    word -> convertLogGroup(word,
                            originDigestMapping.get(JOINER.join(word.getOriginalDigest(), word.getFromLanguage()))));
        }
        return PageModelUtil.transformResult(translateFrequentWordPageResult,
                word -> convertLogGroup(word, null));
    }

    @PostMapping("/updateTransText")
    public BaseResult updateTransText(@RequestBody TransTextDTO dto) {
        ValidateUtil.checkNotNull(dto, "入参为空");
        ValidateUtil.checkTrue(dto.getId() != null && dto.getId() > 0L, "ID不合法");
        ValidateUtil.checkNotBlank(dto.getTextTrans(), "译文为空");
        return new BaseResult(translatedTextService.updateTransText(convertTextDT2B(dto)));
    }

    @PostMapping("/buildCustomTranslate")
    public SimpleResult<Long> buildCustomTranslate(@RequestBody TransTextDTO dto) {
        ValidateUtil.checkNotNull(dto, "入参为空");
        ValidateUtil.checkNotBlank(dto.getTextTrans(), "译文为空");
        ValidateUtil.checkNotBlank(dto.getTextOriginal(), "原文为空");
        ValidateUtil.checkNotBlank(dto.getFromLanguage(), "原始语言为空");
        ValidateUtil.checkNotBlank(dto.getToLanguage(), "译文语音为空");
        dto.setTransEngine(TranslateEngineEnum.CUSTOM.name());
        dto.setOriginalDigest(CryptUtil.digestMd5(dto.getTextOriginal()));
        dto.setTransDigest(CryptUtil.digestMd5(dto.getTextTrans()));
        return new SimpleResult<>(translatedTextService.insertTransText(convertTextDT2B(dto)));
    }

    private TransLogGroup convertLogGroup(TranslateFrequentWord word, List<TranslatedTextBO> textList) {
        return BeanUtil.copy(word, TransLogGroup.class, group -> {
            if (CollectionUtils.isNotEmpty(textList)) {
                group.setFromLanguage(word.getFromLanguage());
                group.setTextOriginal(textList.get(0).getTextOriginal());
                group.setTrnasInfoList(
                        textList.stream()
                                .sorted((Comparator.comparing(bo -> {
                                    TranslateEngineEnum engineEnum = TranslateEngineEnum.getByName(bo.getTransEngine());
                                    if (engineEnum != null) {
                                        return engineEnum.getPriority();
                                    }
                                    return 1;
                                }, Comparator.reverseOrder())))
                                .map(this::convertTextB2DT)
                                .collect(Collectors.toList())
                );
            }
        });
    }

    private TransTextDTO convertTextB2DT(TranslatedTextBO bo) {
        return BeanUtil.copy(bo, TransTextDTO.class);
    }

    private TranslatedTextBO convertTextDT2B(TransTextDTO dto) {
        return BeanUtil.copy(dto, TranslatedTextBO.class);
    }
}
