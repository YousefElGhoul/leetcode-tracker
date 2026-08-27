package com.ghoul.leetcodetracker.client;

import com.ghoul.leetcodetracker.model.external.LeetCodeRequest;
import com.ghoul.leetcodetracker.model.external.LeetCodeResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class LeetCodeClient {
    private static final String QUERY = """
            query userStats($username: String!) {
              matchedUser(username: $username) {
                submitStatsGlobal { acSubmissionNum { difficulty count } }
              }
              allQuestionsCount { difficulty count }
            }
            """;
    private final RestClient restClient;

    public LeetCodeClient(@Qualifier("leetRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public LeetCodeResponse getStats(String username) {
        try {
            return restClient.post()
                    .body(new LeetCodeRequest(QUERY, Map.of("username", username)))
                    .retrieve()
                    .body(LeetCodeResponse.class);
        } catch (ResourceAccessException exception) {
            throw new com.ghoul.leetcodetracker.exception.UpstreamServiceException(
                    "LeetCode did not respond before the request timed out", exception);
        } catch (RestClientException exception) {
            throw new com.ghoul.leetcodetracker.exception.UpstreamServiceException(
                    "LeetCode is currently unavailable", exception);
        }
    }
}
