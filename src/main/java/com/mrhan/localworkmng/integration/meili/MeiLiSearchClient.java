package com.mrhan.localworkmng.integration.meili;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author yuhang
 * @Date 2024-06-06 22:52
 * @Description
 */
@Getter
@Component
public class MeiLiSearchClient {

    private Client client;

    private static final String API_KEY = "defadmin";

    @PostConstruct
    public void init() {
        client = new Client(new Config("http://localhost:7700", API_KEY));
    }

    public Index index(String namespace) {
        return client.index(namespace);
    }

}
