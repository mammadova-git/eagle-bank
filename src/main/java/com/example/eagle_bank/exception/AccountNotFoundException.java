package com.example.eagle_bank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccountNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public AccountNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
