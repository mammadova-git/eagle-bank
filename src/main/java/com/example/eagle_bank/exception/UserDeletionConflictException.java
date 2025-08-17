package com.example.eagle_bank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserDeletionConflictException extends RuntimeException {
    private final HttpStatus status;

    public UserDeletionConflictException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

