package com.mrhan.localworkmng.integration.transcore;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.mrhan.localworkmng.util.JsonUtil;
import com.mrhan.localworkmng.util.ValidateUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yuhang
 * @Date 2024-06-29 18:34
 * @Description
 */
@Component
public class TranslateCoreClient {

    public String translate(String content, String fromLang, String toLang) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", content);
        params.put("from", fromLang);
        params.put("to", toLang);
        HttpRequest request = HttpUtil.createPost("http://localhost:8080/translate");
        request.body(JsonUtil.toJsonString(params), "application/json");
        HttpResponse execute = request.execute();
        ValidateUtil.checkTrue(execute.isOk(), "req is not ok");
        String body = execute.body();
        ValidateUtil.checkNotBlank(body, "body is blank");
        JSONObject obj = JsonUtil.nonNullTryParse(body);
        ValidateUtil.checkTrue(obj.getBooleanValue("success"), "result is not success");
        return obj.getString("result");
    }

}
