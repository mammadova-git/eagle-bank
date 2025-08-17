package com.example.eagle_bank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccountAccessDeniedException extends RuntimeException {
    private final HttpStatus status;

    public AccountAccessDeniedException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
