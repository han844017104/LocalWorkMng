package com.mrhan.localworkmng.core.f95;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mrhan.localworkmng.dal.f95.mapper.F95GameTagMapper;
import com.mrhan.localworkmng.dal.f95.model.F95GameTag;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yuhang
 * @Date 2024-06-29 13:50
 * @Description
 */
@Service
public class F95GameTagService {

    @Resource
    private F95GameTagMapper f95GameTagMapper;

    public List<F95GameTag> queryAll() {
        return f95GameTagMapper.selectList(Wrappers.lambdaQuery()).stream().sorted(Comparator.comparing(F95GameTag::getTagEnName)).collect(Collectors.toList());
    }

    public List<F95GameTag> queryByTagIdList(List<String> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return Collections.emptyList();
        }
        return f95GameTagMapper.selectList(
                Wrappers.<F95GameTag>lambdaQuery()
                        .in(F95GameTag::getTagId, tagIds)
        );
    }

    public void batchUpsert(List<F95GameTag> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        f95GameTagMapper.batchUpsert(list);
    }

}
