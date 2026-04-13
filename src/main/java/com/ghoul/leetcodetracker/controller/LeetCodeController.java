package com.ghoul.leetcodetracker.controller;

import com.ghoul.leetcodetracker.model.dto.StatsResponse;
import com.ghoul.leetcodetracker.service.HeatmapService;
import com.ghoul.leetcodetracker.service.LeetCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LeetCodeController {

    private final LeetCodeService leetCodeService;
    private final HeatmapService heatmapService;

    public LeetCodeController(LeetCodeService leetCodeService, HeatmapService heatmapService) {
        this.leetCodeService = leetCodeService;
        this.heatmapService = heatmapService;
    }

    @ResponseBody
    @PostMapping("/leet")
    public StatsResponse postStats(@RequestParam(required = true) String username) {
        return leetCodeService.getStats(username);
    }

    @PostMapping("/visit")
    public ResponseEntity<Void> recordVisit(@RequestParam(required = true) String username) {
        heatmapService.recordVisit(username);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @GetMapping("/")
    public String test() {
        return "This is a test";
    }
}
