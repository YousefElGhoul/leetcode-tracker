package com.ghoul.leetcodetracker.exception;

public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException() {
        super("Username is already registered");
    }
}
