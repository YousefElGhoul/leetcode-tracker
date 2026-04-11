package com.ghoul.leetcodetracker.model.external;

import java.util.List;

public record LeetCodeResponse(Data data) {

    public record Data(
            MatchedUser matchedUser,
            List<QuestionCount> allQuestionsCount
    ) {}

    public record MatchedUser(SubmitStatsGlobal submitStatsGlobal) {}

    public record SubmitStatsGlobal(List<SubmissionCount> acSubmissionNum) {}

    public record SubmissionCount(String difficulty, int count) {}

    public record QuestionCount(String difficulty, int count) {}
}
