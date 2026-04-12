package com.ghoul.leetcodetracker.model.dto;

import com.ghoul.leetcodetracker.model.entities.Heatmap;
import java.time.LocalDate;

public record HeatmapRecord(LocalDate date, boolean visited, boolean solved) {
    public static HeatmapRecord from(Heatmap heatmap) {
        return new HeatmapRecord(heatmap.getDate(), heatmap.isVisited(), heatmap.isSolved());
    }
}