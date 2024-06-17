package com.mrhan.localworkmng.dal.f95.mapper;

import com.mrhan.localworkmng.dal.f95.model.F95GameRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author MrHan
 * @since 2024-06-04
 */
public interface F95GameRelationMapper extends BaseMapper<F95GameRelation> {

    int batchUpsert(@Param("list") List<F95GameRelation> relations);

}
