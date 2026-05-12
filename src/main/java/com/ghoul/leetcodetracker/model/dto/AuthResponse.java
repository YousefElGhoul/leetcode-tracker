package com.ghoul.leetcodetracker.model.dto;

public record AuthResponse(
        String token,
        long expiresIn,
        String username
) {
}
