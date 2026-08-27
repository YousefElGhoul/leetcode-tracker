package com.ghoul.leetcodetracker.service;

import com.ghoul.leetcodetracker.client.LeetCodeClient;
import com.ghoul.leetcodetracker.exception.LeetCodeUserNotFoundException;
import com.ghoul.leetcodetracker.exception.UpstreamServiceException;
import com.ghoul.leetcodetracker.model.external.LeetCodeResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeetCodeServiceTest {
    private final LeetCodeClient client = mock(LeetCodeClient.class);
    private final HeatmapService heatmap = mock(HeatmapService.class);
    private final LeetCodeService service = new LeetCodeService(client, heatmap);

    @Test
    void mapsStatsAndUpdatesSnapshotBeforeReadingHeatmap() {
        when(client.getStats("alice")).thenReturn(response());
        when(heatmap.getHeatmap("alice", LocalDate.now().withDayOfMonth(1), LocalDate.now()))
                .thenReturn(List.of());

        var result = service.getStats("alice");

        assertThat(result.progress().all().solvedNum()).isEqualTo(20);
        assertThat(result.progress().easy().solvedNum()).isEqualTo(10);
        InOrder order = inOrder(heatmap);
        order.verify(heatmap).upsertActivity("alice", 20);
        order.verify(heatmap).getHeatmap("alice", LocalDate.now().withDayOfMonth(1), LocalDate.now());
    }

    @Test
    void missingUserIsNotFound() {
        when(client.getStats("missing")).thenReturn(new LeetCodeResponse(
                new LeetCodeResponse.Data(null, List.of()), List.of()));

        assertThatThrownBy(() -> service.getStats("missing"))
                .isInstanceOf(LeetCodeUserNotFoundException.class);
    }

    @Test
    void malformedResponseIsBadGateway() {
        when(client.getStats("alice")).thenReturn(new LeetCodeResponse(null, List.of()));

        assertThatThrownBy(() -> service.getStats("alice"))
                .isInstanceOf(UpstreamServiceException.class);
    }

    private LeetCodeResponse response() {
        var submissions = List.of(
                new LeetCodeResponse.SubmissionCount("All", 20),
                new LeetCodeResponse.SubmissionCount("Easy", 10),
                new LeetCodeResponse.SubmissionCount("Medium", 7),
                new LeetCodeResponse.SubmissionCount("Hard", 3));
        var totals = List.of(
                new LeetCodeResponse.QuestionCount("All", 3000),
                new LeetCodeResponse.QuestionCount("Easy", 800),
                new LeetCodeResponse.QuestionCount("Medium", 1600),
                new LeetCodeResponse.QuestionCount("Hard", 600));
        return new LeetCodeResponse(new LeetCodeResponse.Data(
                new LeetCodeResponse.MatchedUser(new LeetCodeResponse.SubmitStatsGlobal(submissions)), totals),
                List.of());
    }
}
