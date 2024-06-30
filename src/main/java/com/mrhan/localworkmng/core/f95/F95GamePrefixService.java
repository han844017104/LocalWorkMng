package com.mrhan.localworkmng.core.f95;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mrhan.localworkmng.dal.f95.mapper.F95GamePrefixMapper;
import com.mrhan.localworkmng.dal.f95.model.F95GamePrefix;
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
public class F95GamePrefixService {

    @Resource
    private F95GamePrefixMapper f95GamePrefixMapper;

    public List<F95GamePrefix> queryByType(String type) {
        return f95GamePrefixMapper.selectList(
                Wrappers.<F95GamePrefix>lambdaQuery().eq(F95GamePrefix::getPrefixType, type)
        ).stream().sorted(Comparator.comparing(F95GamePrefix::getPrefixEnName)).collect(Collectors.toList());
    }

    public List<F95GamePrefix> queryByPidList(List<String> pidList) {
        if (CollectionUtils.isEmpty(pidList)) {
            return Collections.emptyList();
        }
        return f95GamePrefixMapper.selectList(
                Wrappers.<F95GamePrefix>lambdaQuery().in(F95GamePrefix::getPrefixId, pidList)
        );
    }

    public void batchUpsert(List<F95GamePrefix> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        f95GamePrefixMapper.batchUpsert(list);
    }
}
