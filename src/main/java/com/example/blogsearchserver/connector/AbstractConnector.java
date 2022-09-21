package com.example.blogsearchserver.connector;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public abstract class AbstractConnector {

    protected WebClient getWebClient(String baseUrl) {

        return WebClient.create(baseUrl);
    }
}
