package com.mrhan.localworkmng.dal.f95.mapper;

import com.mrhan.localworkmng.dal.f95.model.F95Game;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * f95游戏表 Mapper 接口
 * </p>
 *
 * @author MrHan
 * @since 2024-06-04
 */
public interface F95GameMapper extends BaseMapper<F95Game> {

    int upsert(F95Game game);

    int batchUpsert(@Param("list") List<F95Game> games);

}
