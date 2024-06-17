package com.mrhan.localworkmng.dal.f95.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * f95游戏表
 * </p>
 *
 * @author MrHan
 * @since 2024-06-04
 */
@TableName("f95_game")
@ApiModel(value = "F95Game对象", description = "f95游戏表")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class F95Game implements Serializable {

    @Serial
    private static final long serialVersionUID = 2026622317263584349L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String threadId;

    private String title;

    private String gameVersion;

    private Long views;

    private Long likes;

    private BigDecimal likeRatio;

    private BigDecimal rating;

    private Date gameUpdateDate;

    private Date gmtCreate;

    private Date gmtModified;

    private String status;

    private String extInfo;

    @Override
    public String toString() {
        return "F95Game{" +
                "id = " + id +
                ", threadId = " + threadId +
                ", title = " + title +
                ", gameVersion = " + gameVersion +
                ", views = " + views +
                ", likes = " + likes +
                ", likeRatio = " + likeRatio +
                ", rating = " + rating +
                ", gameUpdateDate = " + gameUpdateDate +
                ", gmtCreate = " + gmtCreate +
                ", gmtModified = " + gmtModified +
                ", status = " + status +
                ", extInfo = " + extInfo +
                "}";
    }
}
