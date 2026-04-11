package com.ghoul.leetcodetracker.config;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${leetcode-graphql.base-url}")
    private String leetcodeGraphQLBaseUrl;

    @Bean
    public RestClient leetRestClient() {
        return getRestClient(leetcodeGraphQLBaseUrl);
    }

    @NonNull
    private RestClient getRestClient(String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    System.out.println(">>> Method : " + request.getMethod());
                    System.out.println(">>> URL    : " + request.getURI());
                    System.out.println(">>> Headers: " + request.getHeaders());

                    ClientHttpResponse response = execution.execute(request, body);

                    System.out.println("<<< Status : " + response.getStatusCode());
                    return response;
                })
                .build();
    }
}