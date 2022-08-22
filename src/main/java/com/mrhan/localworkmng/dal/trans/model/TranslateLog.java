package com.mrhan.localworkmng.dal.trans.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 翻译日志
 * </p>
 *
 * @author MrHan
 * @since 2022-08-09
 */
@TableName("translate_log")
@Data
public class TranslateLog {

    private Long id;

    private LocalDateTime actionTime;

    private String dt;

    private String originalDigest;

}
