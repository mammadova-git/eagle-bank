package com.example.eagle_bank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserAccessDeniedException extends RuntimeException {

    private final HttpStatus status;
    private final Long requestedUserId;

    public UserAccessDeniedException(String message, HttpStatus status, Long requestedUserId) {
        super(message);
        this.status = status;
        this.requestedUserId = requestedUserId;
    }
}
