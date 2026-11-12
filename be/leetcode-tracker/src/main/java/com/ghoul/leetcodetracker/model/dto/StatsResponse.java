package com.ghoul.leetcodetracker.model.dto;

import java.util.List;

public record StatsResponse(
        Progress progress,
        List<HeatmapRecord> heatmap
) {
    public record Progress(
            Difficulty all,
            Difficulty easy,
            Difficulty medium,
            Difficulty hard
    ) {}
    public record Difficulty(
            int solvedNum,
            int totalNum
    ) {}
}
