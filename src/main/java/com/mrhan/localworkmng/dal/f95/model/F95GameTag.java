package com.mrhan.localworkmng.dal.f95.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author MrHan
 * @since 2024-06-04
 */
@TableName("f95_game_tag")
@ApiModel(value = "F95GameTag对象", description = "")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class F95GameTag implements Serializable {

    @Serial
    private static final long serialVersionUID = 283863471634301128L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String tagId;

    private String tagEnName;

    private String tagChName;

    @Override
    public String toString() {
        return "F95GameTag{" +
            "id = " + id +
            ", tagId = " + tagId +
            ", tagEnName = " + tagEnName +
            ", tagChName = " + tagChName +
        "}";
    }
}
