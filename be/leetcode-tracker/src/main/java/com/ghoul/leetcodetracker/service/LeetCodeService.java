package com.ghoul.leetcodetracker.service;

import com.ghoul.leetcodetracker.client.LeetCodeClient;
import com.ghoul.leetcodetracker.model.dto.StatsResponse;
import com.ghoul.leetcodetracker.model.external.LeetCodeResponse;
import com.ghoul.leetcodetracker.exception.LeetCodeUserNotFoundException;
import com.ghoul.leetcodetracker.exception.UpstreamServiceException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LeetCodeService {

    private final LeetCodeClient leetCodeClient;
    private final HeatmapService heatmapService;


    public LeetCodeService(LeetCodeClient leetCodeClient, HeatmapService heatmapService) {
        this.leetCodeClient = leetCodeClient;
        this.heatmapService = heatmapService;
    }

    /**
     * Retrieves comprehensive LeetCode statistics for the specified user, including problem-solving
     * progress across all difficulty levels and a heatmap of recent activity for the current month.
     * As a side effect, this method also records today's solving activity by comparing the current
     * total with the most recent snapshot in the database.
     *
     * @param username the LeetCode username to fetch statistics for
     * @return a {@link StatsResponse} containing progress data (total, easy, medium, hard) and
     * a heatmap of activity from the first day of the current month to today
     */
    
    public StatsResponse getStats(String username) {

        LocalDate TODAY = LocalDate.now();

        LeetCodeResponse response = leetCodeClient.getStats(username);

        if (response == null || response.data() == null) {
            throw new UpstreamServiceException("LeetCode returned a malformed response");
        }
        if (response.errors() != null && !response.errors().isEmpty()) {
            throw new UpstreamServiceException("LeetCode rejected the statistics request");
        }
        if (response.data().matchedUser() == null) {
            throw new LeetCodeUserNotFoundException();
        }
        if (response.data().matchedUser().submitStatsGlobal() == null
                || response.data().matchedUser().submitStatsGlobal().acSubmissionNum() == null
                || response.data().allQuestionsCount() == null) {
            throw new UpstreamServiceException("LeetCode returned a malformed response");
        }

        List<LeetCodeResponse.SubmissionCount> submissions = response.data()
                .matchedUser()
                .submitStatsGlobal()
                .acSubmissionNum();

        List<LeetCodeResponse.QuestionCount> totals = response.data().allQuestionsCount();

        int currentTotal = submissions.stream()
                .filter(e -> "All".equals(e.difficulty()))
                .mapToInt(LeetCodeResponse.SubmissionCount::count)
                .findFirst()
                .orElse(0);

        heatmapService.upsertActivity(username, currentTotal);

        return new StatsResponse(
                new StatsResponse.Progress(
                        buildDifficulty("All", submissions, totals),
                        buildDifficulty("Easy", submissions, totals),
                        buildDifficulty("Medium", submissions, totals),
                        buildDifficulty("Hard", submissions, totals)
                ),
                heatmapService.getHeatmap(username, TODAY.withDayOfMonth(1), TODAY)
        );

    }

    /**
     * Helper function that constructs a {@link StatsResponse.Difficulty} object for a specific
     * difficulty level by extracting the number of solved problems from the submissions list
     * and the total available problems from the totals list.
     *
     * @param difficulty  the difficulty level to filter by (e.g., "All", "Easy", "Medium", "Hard")
     * @param submissions the list of submission counts from LeetCode API
     * @param totals      the list of total question counts from LeetCode API
     * @return a {@link StatsResponse.Difficulty} containing solved and total problem counts
     */

    private StatsResponse.Difficulty buildDifficulty(
            String difficulty,
            List<LeetCodeResponse.SubmissionCount> submissions,
            List<LeetCodeResponse.QuestionCount> totals
    ) {
        int solved = submissions.stream()
                .filter(e -> difficulty.equals(e.difficulty()))
                .mapToInt(LeetCodeResponse.SubmissionCount::count)
                .findFirst()
                .orElse(0);

        int total = totals.stream()
                .filter(e -> difficulty.equals(e.difficulty()))
                .mapToInt(LeetCodeResponse.QuestionCount::count)
                .findFirst()
                .orElse(0);

        return new StatsResponse.Difficulty(solved, total);
    }
}
