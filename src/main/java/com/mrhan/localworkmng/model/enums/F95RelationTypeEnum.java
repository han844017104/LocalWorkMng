package com.mrhan.localworkmng.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @Author yuhang
 * @Date 2024-06-04 22:35
 * @Description
 */
@Getter
public enum F95RelationTypeEnum {
    TAG("tag", "标签关系"),

    PREFIX("prefix", "前缀关系"),

    ;

    private final String code;

    private final String desc;

    F95RelationTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static F95RelationTypeEnum fromCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(F95RelationTypeEnum.values()).filter(e -> e.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
