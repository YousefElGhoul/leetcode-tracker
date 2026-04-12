package com.ghoul.leetcodetracker.service;

import com.ghoul.leetcodetracker.client.LeetCodeClient;
import com.ghoul.leetcodetracker.model.dto.StatsResponse;
import com.ghoul.leetcodetracker.model.external.LeetCodeResponse;
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

        StatsResponse stats = new StatsResponse(
                new StatsResponse.Progress(
                        buildDifficulty("All", submissions, totals),
                        buildDifficulty("Easy", submissions, totals),
                        buildDifficulty("Medium", submissions, totals),
                        buildDifficulty("Hard", submissions, totals)
                ),
                heatmapService.getHeatmap(username, TODAY.withDayOfMonth(1), TODAY)
        );

        // Side effect: records today's activity for the heatmap
        heatmapService.upsertActivity(username, currentTotal);

        return stats;
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
