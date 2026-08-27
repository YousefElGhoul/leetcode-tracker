package com.ghoul.leetcodetracker.controller;

import com.ghoul.leetcodetracker.model.dto.StatsResponse;
import com.ghoul.leetcodetracker.service.HeatmapService;
import com.ghoul.leetcodetracker.service.LeetCodeService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class LeetCodeController {

    private final LeetCodeService leetCodeService;
    private final HeatmapService heatmapService;

    @GetMapping("/tracker")
    public StatsResponse getStats(
            @RequestParam
            @Pattern(regexp = "[A-Za-z0-9_-]+", message = "Username may contain only letters, numbers, underscores, and hyphens")
            @Size(max = 50, message = "Username must be at most 50 characters")
            String username
    ) {
        return leetCodeService.getStats(username);
    }

    @PostMapping("/visit")
    public ResponseEntity<Void> recordVisit(
            @RequestParam
            @Pattern(regexp = "[A-Za-z0-9_-]+", message = "Username may contain only letters, numbers, underscores, and hyphens")
            @Size(max = 50, message = "Username must be at most 50 characters")
            String username
    ) {
        heatmapService.recordVisit(username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearHeatmap(Authentication authentication) {
        heatmapService.clearHeatmap(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
