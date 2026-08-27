package com.ghoul.leetcodetracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${leetcode-graphql.base-url}")
    private String leetcodeGraphQLBaseUrl;

    @Value("${leetcode-graphql.connect-timeout}")
    private java.time.Duration connectTimeout;

    @Value("${leetcode-graphql.read-timeout}")
    private java.time.Duration readTimeout;

    @Bean
    public RestClient leetRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return RestClient.builder()
                .baseUrl(leetcodeGraphQLBaseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
