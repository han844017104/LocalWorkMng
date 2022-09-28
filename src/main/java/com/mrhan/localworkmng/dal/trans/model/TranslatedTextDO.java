package com.mrhan.localworkmng.dal.trans.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author MrHan
 * @since 2022-08-09
 */
@TableName("translated_text")
@Data
public class TranslatedTextDO implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Date gmtCreate;

    private String fromLanguage;

    private String toLanguage;

    private String textOriginal;

    private String textTrans;

    private String transEngine;

    private String originalDigest;

    private String transDigest;

    private String extInfo;

}
