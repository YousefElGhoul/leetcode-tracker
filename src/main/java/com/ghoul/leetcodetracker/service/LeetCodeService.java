package com.ghoul.leetcodetracker.service;

import org.springframework.stereotype.Service;

@Service
public class LeetCodeService {
    public String getStats(String username) {
        return "Stats for user: " + username;
    }
}
