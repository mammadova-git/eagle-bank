package com.example.eagle_bank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccountNotFoundException extends RuntimeException {
    private final HttpStatus status;
    private final Long accountId;

    public AccountNotFoundException(String message, HttpStatus status, Long accountId) {
        super(message);
        this.status = status;
        this.accountId = accountId;
    }
}
