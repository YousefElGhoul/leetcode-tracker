package com.ghoul.leetcodetracker.client;

import com.ghoul.leetcodetracker.exception.UpstreamServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class LeetCodeClientTest {

    @Test
    void sendsUsernameAsGraphQlVariableAndParsesResponse() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://leetcode.test/graphql");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://leetcode.test/graphql"))
                .andExpect(content().json("""
                        {"variables":{"username":"alice"}}
                        """, false))
                .andRespond(withSuccess("""
                        {"data":{"matchedUser":{"submitStatsGlobal":{"acSubmissionNum":[{"difficulty":"All","count":12}]}},"allQuestionsCount":[]}}
                        """, MediaType.APPLICATION_JSON));

        var response = new LeetCodeClient(builder.build()).getStats("alice");

        assertThat(response.data().matchedUser().submitStatsGlobal().acSubmissionNum().getFirst().count())
                .isEqualTo(12);
        server.verify();
    }

    @Test
    void translatesHttpAndMalformedJsonFailures() {
        RestClient.Builder unavailableBuilder = RestClient.builder().baseUrl("https://leetcode.test/graphql");
        MockRestServiceServer unavailableServer = MockRestServiceServer.bindTo(unavailableBuilder).build();
        unavailableServer.expect(requestTo("https://leetcode.test/graphql")).andRespond(withServerError());

        assertThatThrownBy(() -> new LeetCodeClient(unavailableBuilder.build()).getStats("alice"))
                .isInstanceOf(UpstreamServiceException.class)
                .hasMessage("LeetCode is currently unavailable");

        RestClient.Builder malformedBuilder = RestClient.builder().baseUrl("https://leetcode.test/graphql");
        MockRestServiceServer malformedServer = MockRestServiceServer.bindTo(malformedBuilder).build();
        malformedServer.expect(requestTo("https://leetcode.test/graphql"))
                .andRespond(withSuccess("not-json", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> new LeetCodeClient(malformedBuilder.build()).getStats("alice"))
                .isInstanceOf(UpstreamServiceException.class);
    }
}
