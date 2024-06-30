package com.mrhan.localworkmng.dal.f95.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author MrHan
 * @since 2024-06-04
 */
@TableName("f95_game_prefix")
@ApiModel(value = "F95GamePrefix对象", description = "")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class F95GamePrefix implements Serializable {

    @Serial
    private static final long serialVersionUID = 3819608455584394258L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String prefixId;

    private String prefixEnName;

    private String prefixChName;

    private String prefixType;

    @Override
    public String toString() {
        return "F95GamePrefix{" +
                "id = " + id +
                ", prefixId = " + prefixId +
                ", prefixEnName = " + prefixEnName +
                ", prefixChName = " + prefixChName +
                ", prefixType = " + prefixType +
                "}";
    }
}
