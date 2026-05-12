package com.ghoul.leetcodetracker.controller;

import com.ghoul.leetcodetracker.model.dto.StatsResponse;
import com.ghoul.leetcodetracker.service.HeatmapService;
import com.ghoul.leetcodetracker.service.LeetCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LeetCodeController {

    private final LeetCodeService leetCodeService;
    private final HeatmapService heatmapService;

    @ResponseBody
    @GetMapping("/tracker")
    public StatsResponse getStats(@RequestParam(required = true) String username) {
        return leetCodeService.getStats(username);
    }

    @ResponseBody
    @PostMapping("/visit")
    public ResponseEntity<Void> recordVisit(@RequestParam(required = true) String username) {
        heatmapService.recordVisit(username);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearHeatmap(@RequestParam(required = true) String username) {
        heatmapService.clearHeatmap(username);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @GetMapping("/test-header")
    public String test() {
        return "<h1>This is a test</h1>";
    }
}
