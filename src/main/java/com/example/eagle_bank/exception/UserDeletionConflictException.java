package com.example.eagle_bank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserDeletionConflictException extends RuntimeException {
    private final HttpStatus status;
    private final Long userId;

    public UserDeletionConflictException(String message, HttpStatus status, Long userId) {
        super(message);
        this.status = status;
        this.userId = userId;
    }
}

