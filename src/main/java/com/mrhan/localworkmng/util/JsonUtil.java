/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONValidator;
import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Author yuhang
 * @Date 2022-12-20 14:24
 * @Description
 */
public class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    public static String toJsonString(Object o, String... excludeColumns) {
        if (o == null) {
            return "{}";
        }
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        if (excludeColumns != null && excludeColumns.length > 0) {
            filter.getExcludes().addAll(Lists.newArrayList(excludeColumns));
        }
        return JSON.toJSONString(o, filter);
    }

    public static JSONObject nonNullTryParse(String str) {
        JSONObject jsonObject = tryParse(str);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    public static JSONObject tryParse(String str) {
        JSONObject ret = null;
        if (StrUtil.isBlank(str)) {
            return null;
        }
        try {
            ret = JSONObject.parseObject(str);
        } catch (Exception e) {
            LoggerUtil.error(LOGGER, e, "[util.json.tryParse](parse error)");
        }
        return ret;
    }

    public static JSONArray tryParseArray(String str) {
        JSONArray array = null;
        if (StrUtil.isBlank(str)) {
            return null;
        }
        try {
            array = JSONArray.parseArray(str);
        } catch (Exception e) {
            LoggerUtil.warn(LOGGER, e, "[util.json.tryParseArray](parse error)");
        }
        return array;
    }

    public static JSONArray tryParseArrayDefaultEmpty(String str) {
        JSONArray array = null;
        if (StrUtil.isBlank(str)) {
            return new JSONArray();
        }
        try {
            array = JSONArray.parseArray(str);
        } catch (Exception e) {
            LoggerUtil.warn(LOGGER, e, "[util.json.tryParseArray](parse error)");
        }
        return array == null ? new JSONArray() : array;
    }

    public static Map<String, String> parseMap(String str) {
        return parseMap(tryParse(str));
    }

    public static Map<String, String> parseMap(JSONObject obj) {
        Map<String, String> map = Maps.newHashMap();
        if (obj == null) {
            return map;
        }
        obj.forEach((k, v) -> {
            if (v instanceof String || v instanceof Number) {
                map.put(k, v.toString());
            } else if (v != null) {
                map.put(k, toJsonString(v));
            }
        });
        return map;
    }

    public static boolean isJsonArray(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        try {
            JSONArray.parseArray(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isJson(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        boolean firstValidate = JSONValidator.from(str).validate() &&
                !NumberUtil.isNumber(str) &&
                !StrUtil.equalsIgnoreCase(StrUtil.trim(str), Boolean.TRUE.toString()) &&
                !StrUtil.equalsIgnoreCase(StrUtil.trim(str), Boolean.FALSE.toString());
        if (!firstValidate) {
            return false;
        }
        String trim = StrUtil.trim(str);
        return (trim.startsWith("{") && trim.endsWith("}")) ||
                (trim.startsWith("[") && trim.endsWith("]"));
    }

    public static Object deepUnpack(String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        boolean isArray = isJsonArray(str);
        if (isArray) {
            JSONArray array = new JSONArray();
            JSONArray root = tryParseArrayDefaultEmpty(str);
            for (int i = 0; i < root.size(); i++) {
                String value = root.getString(i);
                if (isJson(value)) {
                    try {
                        array.add(deepUnpack(value));
                    } catch (Exception e) {
                        LoggerUtil.warn(LOGGER, e, "[util.json.deepUnpack](arr)(value parse error)({})", value);
                        array.add(value);
                    }
                } else {
                    array.add(value);
                }
            }
            return array;
        } else {
            JSONObject result = new JSONObject();
            JSONObject root = tryParse(str);
            if (root != null) {
                for (String key : root.keySet()) {
                    String value = root.getString(key);
                    if (isJson(value)) {
                        try {
                            result.put(key, deepUnpack(value));
                        } catch (Exception e) {
                            LoggerUtil.warn(LOGGER, e, "[util.json.deepUnpack](obj)(value parse error)({})", value);
                            result.put(key, value);
                        }
                    } else {
                        result.put(key, value);
                    }
                }
            }
            return result;
        }
    }

    public static String deepUnpack2Str(Object o) {
        if (o == null) {
            return null;
        }
        return toJsonString(deepUnpack(toJsonString(o)));
    }


}
