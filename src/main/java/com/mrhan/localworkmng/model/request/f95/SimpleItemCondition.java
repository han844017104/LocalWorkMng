package com.mrhan.localworkmng.model.request.f95;

import com.mrhan.localworkmng.model.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Author yuhang
 * @Date 2024-06-18 21:07
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
@ApiModel
public class SimpleItemCondition extends ToString {
    @Serial
    private static final long serialVersionUID = -2893380077303128063L;

    @ApiModelProperty("标签或前缀的ID")
    private String id;

    @ApiModelProperty("是否为等值判断，false即表示为不等于该ID")
    private boolean eq;
}
