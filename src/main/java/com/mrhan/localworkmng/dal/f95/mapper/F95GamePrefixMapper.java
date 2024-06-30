package com.mrhan.localworkmng.dal.f95.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mrhan.localworkmng.dal.f95.model.F95GamePrefix;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author MrHan
 * @since 2024-06-04
 */
public interface F95GamePrefixMapper extends BaseMapper<F95GamePrefix> {
    int batchUpsert(@Param("list") List<F95GamePrefix> list);
}
