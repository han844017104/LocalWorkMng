package com.mrhan.localworkmng.model.response.f95;

import com.mrhan.localworkmng.model.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Author yuhang
 * @Date 2024-06-18 20:57
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
@ApiModel
public class F95GameTagViewInfo extends ToString {
    @Serial
    private static final long serialVersionUID = -5544735867553753655L;

    @ApiModelProperty("标签ID")
    private String tagId;

    @ApiModelProperty("标签英文名")
    private String tagEnName;

    @ApiModelProperty("标签中文名")
    private String tagChName;
}
