package com.mrhan.localworkmng.model.request.f95;

import com.mrhan.localworkmng.model.ToString;
import com.mrhan.localworkmng.model.request.CommonSortOrder;
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
public class F95GameSearchParam extends ToString {

    private String title;

    private String threadId;

    private Long minView;

    private Long maxView;

    private Long minLike;

    private Long maxLike;

    private BigDecimal minLikeRatio;

    private BigDecimal maxLikeRatio;

    private BigDecimal minRating;

    private BigDecimal maxRating;

    private Long minGameUpdateDate;

    private Long maxGameUpdateDate;

    private List<ItemCondition> itemConditions;

    private List<CommonSortOrder> orders;


}
