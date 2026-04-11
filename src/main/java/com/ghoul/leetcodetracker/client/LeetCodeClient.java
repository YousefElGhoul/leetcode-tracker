package com.ghoul.leetcodetracker.client;

import com.ghoul.leetcodetracker.model.external.LeetCodeRequest;
import com.ghoul.leetcodetracker.model.external.LeetCodeResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class LeetCodeClient {
    private final RestClient restClient;

    public LeetCodeClient(@Qualifier("leetRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public LeetCodeResponse getStats(String username) {
        String QUERY = String.format("query { matchedUser(username: \"%s\") { submitStatsGlobal { acSubmissionNum { difficulty count } } } allQuestionsCount { difficulty count } }", username);

        System.out.println("Getting stats for user: " + username);

        LeetCodeRequest request = new LeetCodeRequest(QUERY);

        return restClient.post()
                .body(request)
                .retrieve()
                .body(LeetCodeResponse.class);
    }
}
