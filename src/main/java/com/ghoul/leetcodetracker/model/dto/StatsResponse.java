package com.ghoul.leetcodetracker.model.dto;

public record StatsResponse(
        Difficulty all,
        Difficulty easy,
        Difficulty medium,
        Difficulty hard
) {
    public record Difficulty(
            int solvedNum,
            int totalNum
    ) {}
}
