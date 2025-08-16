package com.example.eagle_bank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthenticationFailedException extends RuntimeException {

    private final HttpStatus status;

    public AuthenticationFailedException(String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
    }
}