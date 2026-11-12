package com.ghoul.leetcodetracker.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Pattern(regexp = "[A-Za-z0-9_-]+", message = "Username may contain only letters, numbers, underscores, and hyphens")
        @Size(max = 50, message = "Username must be at most 50 characters")
        String username,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        String password
) {
}
