package com.ghoul.leetcodetracker.service;

import com.ghoul.leetcodetracker.client.LeetCodeClient;
import com.ghoul.leetcodetracker.model.dto.StatsResponse;
import com.ghoul.leetcodetracker.model.external.LeetCodeResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeetCodeService {

    private final LeetCodeClient leetCodeClient;

    public LeetCodeService(LeetCodeClient leetCodeClient) {
        this.leetCodeClient = leetCodeClient;
    }

    public StatsResponse getStats(String username) {
        LeetCodeResponse response = leetCodeClient.getStats(username);
        List<LeetCodeResponse.SubmissionCount> submissionCounts = response.data().matchedUser().submitStatsGlobal().acSubmissionNum();
        return new StatsResponse(
                new StatsResponse.Difficulty(
                        submissionCounts.get(0).count(),
                        response.data().allQuestionsCount().get(0).count()
                ),
                new StatsResponse.Difficulty(
                        submissionCounts.get(1).count(),
                        response.data().allQuestionsCount().get(1).count()
                ),
                new StatsResponse.Difficulty(
                        submissionCounts.get(2).count(),
                        response.data().allQuestionsCount().get(2).count()
                ),
                new StatsResponse.Difficulty(
                        submissionCounts.get(3).count(),
                        response.data().allQuestionsCount().get(3).count()
                )
        );
    }
}
