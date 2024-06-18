package com.mrhan.localworkmng.model.request.f95;

import com.mrhan.localworkmng.model.ToString;
import com.mrhan.localworkmng.model.request.CommonSortOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author yuhang
 * @Date 2024-06-06 23:21
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
@ApiModel
public class F95GameSearchParam extends ToString {

    /**
     * 模糊查询
     */
    @ApiModelProperty("模糊查询")
    private String fuzzy;

    private String threadId;

    /**
     * 最小浏览数
     */
    @ApiModelProperty("最小浏览数")
    private Long minView;

    /**
     * 最大浏览数
     */
    @ApiModelProperty("最大浏览数")
    private Long maxView;

    /**
     * 最小点赞数
     */
    @ApiModelProperty("最小点赞数")
    private Long minLike;

    /**
     * 最大点赞数
     */
    @ApiModelProperty("最大点赞数")
    private Long maxLike;

    /**
     * 最小点赞率
     */
    @ApiModelProperty("最小点赞率")
    private BigDecimal minLikeRatio;

    /**
     * 最大点赞率
     */
    @ApiModelProperty("最大点赞率")
    private BigDecimal maxLikeRatio;

    /**
     * 最小分数
     */
    @ApiModelProperty("最小分数")
    private BigDecimal minRating;

    /**
     * 最大数
     */
    @ApiModelProperty("最大数")
    private BigDecimal maxRating;

    /**
     * 最早更新日期
     */
    @ApiModelProperty("最早更新日期")
    private Long minGameUpdateDate;
    /**
     * 最晚更新日期
     */
    @ApiModelProperty("最晚更新日期")
    private Long maxGameUpdateDate;

    /**
     * 游戏状态条件
     */
    @ApiModelProperty("游戏状态条件")
    private List<SimpleItemCondition> gameStatusConditions;

    /**
     * 游戏引擎条件
     */
    @ApiModelProperty("游戏引擎条件")
    private List<SimpleItemCondition> gameEngineConditions;

    /**
     * 游戏前缀条件
     */
    @ApiModelProperty("游戏前缀条件")
    private List<SimpleItemCondition> gamePrefixConditions;

    /**
     * 游戏标签条件
     */
    @ApiModelProperty("游戏标签条件")
    private List<SimpleBatchItemCondition> gameTagConditions;

    private List<ItemCondition> itemConditions;

    /**
     * 排序规则
     */
    @ApiModelProperty("排序规则")
    private List<CommonSortOrder> orders;


}
