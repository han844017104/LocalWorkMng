package com.mrhan.localworkmng.model.response.f95;

import com.mrhan.localworkmng.model.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Author yuhang
 * @Date 2024-06-18 20:58
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
@ApiModel
public class F95GamePrefixViewInfo extends ToString {
    @Serial
    private static final long serialVersionUID = -8252845291539689740L;

    @ApiModelProperty("前缀ID")
    private String prefixId;

    @ApiModelProperty("前缀英文名")
    private String prefixEnName;

    @ApiModelProperty("前缀中文名")
    private String prefixChName;

    @ApiModelProperty("前缀类型")
    private String prefixType;

}
