package com.ghoul.leetcodetracker.controller;

import com.ghoul.leetcodetracker.model.dto.StatsResponse;
import com.ghoul.leetcodetracker.service.LeetCodeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LeetCodeController {

    private final LeetCodeService leetCodeService;

    public LeetCodeController(LeetCodeService leetCodeService) {
        this.leetCodeService = leetCodeService;
    }

    @ResponseBody
    @PostMapping("/leet")
    public StatsResponse postStats(@RequestParam(required = true) String username) {
        return leetCodeService.getStats(username);
    }

    @ResponseBody
    @GetMapping("/")
    public String test() {
        return "This is a test";
    }
}
