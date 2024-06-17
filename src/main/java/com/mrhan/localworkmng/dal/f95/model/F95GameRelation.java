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
@TableName("f95_game_relation")
@ApiModel(value = "F95GameRelation对象", description = "")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class F95GameRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = -1493197348589756413L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String outId;

    private String tid;

    private String relationType;

    @Override
    public String toString() {
        return "F95GameRelation{" +
            "id = " + id +
            ", outId = " + outId +
            ", tid = " + tid +
            ", relationType = " + relationType +
        "}";
    }
}
