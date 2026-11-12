package com.ghoul.leetcodetracker.model.external;

import java.util.Map;

public record LeetCodeRequest(
        String query,
        Map<String, String> variables
) {
}
