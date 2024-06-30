package com.mrhan.localworkmng.model.response.f95;

import com.mrhan.localworkmng.model.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author yuhang
 * @Date 2024-06-18 20:55
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
@ApiModel
public class F95GameViewInfo extends ToString {
    @Serial
    private static final long serialVersionUID = 8525535641411043821L;

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("线程ID")
    private String threadId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("中文标题")
    private String chTitle;

    @ApiModelProperty("游戏版本")
    private String gameVersion;

    @ApiModelProperty("浏览数")
    private long views;

    @ApiModelProperty("点赞数")
    private long likes;

    @ApiModelProperty("点赞率")
    private BigDecimal likeRatio;

    @ApiModelProperty("打分")
    private BigDecimal rating;

    @ApiModelProperty("游戏更新时间")
    private Date gameUpdateDate;

    @ApiModelProperty("数据录入时间")
    private Date gmtCreate;

    @ApiModelProperty("数据更新时间")
    private Date gmtModified;

    @ApiModelProperty("数据状态")
    private String status;

    @ApiModelProperty("游戏预览列表")
    private List<String> previews;

    @ApiModelProperty("游戏主预览图")
    private String mainPreview;

    @ApiModelProperty("游戏状态")
    private List<F95GamePrefixViewInfo> gameStatus;

    @ApiModelProperty("游戏引擎")
    private List<F95GamePrefixViewInfo> gameEngine;

    @ApiModelProperty("游戏前缀列表")
    private List<F95GamePrefixViewInfo> prefixes = new ArrayList<>();

    @ApiModelProperty("游戏标签列表")
    private List<F95GameTagViewInfo> tags = new ArrayList<>();
}
