package com.example.eagle_bank.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String errorCode;
    private String message;
    private Long userId;

    public ErrorResponse(String errorCode, String message, Long userId) {
        this.errorCode = errorCode;
        this.message = message;
        this.userId = userId;
    }
}

