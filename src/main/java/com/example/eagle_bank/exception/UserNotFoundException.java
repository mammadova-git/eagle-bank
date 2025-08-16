package com.example.eagle_bank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final HttpStatus status;
    private final Long userId;

    public UserNotFoundException(String message, HttpStatus status, Long userId) {
        super(message);
        this.status = status;
        this.userId = userId;
    }
}
