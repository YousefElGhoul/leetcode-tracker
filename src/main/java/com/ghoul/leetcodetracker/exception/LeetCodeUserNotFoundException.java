package com.ghoul.leetcodetracker.exception;

public class LeetCodeUserNotFoundException extends RuntimeException {
    public LeetCodeUserNotFoundException() {
        super("LeetCode user was not found");
    }
}
