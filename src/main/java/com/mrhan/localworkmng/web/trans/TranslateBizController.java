/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.web.trans;

import com.mrhan.localworkmng.core.trans.TranslatedTextService;
import com.mrhan.localworkmng.model.bo.TranslatedTextBO;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.request.trans.TransTextQueryParam;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.model.response.trans.TransTextDTO;
import com.mrhan.localworkmng.util.BeanUtil;
import com.mrhan.localworkmng.util.PageModelUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author yuhang
 * @Date 2022-08-22 11:05
 * @Description
 */
@RestController
@RequestMapping("/trans")
public class TranslateBizController {

    @Resource
    private TranslatedTextService translatedTextService;

    @PostMapping("/queryTransText")
    public PageResult<TransTextDTO> query(@RequestBody PageRequest<TransTextQueryParam> request) {
        PageResult<TranslatedTextBO> result = translatedTextService.query(request);
        return PageModelUtil.transformResult(result, this::convertTextB2DT);
    }

    private TransTextDTO convertTextB2DT(TranslatedTextBO bo) {
        return BeanUtil.copy(bo, TransTextDTO.class);
    }

}
