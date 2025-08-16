package com.example.eagle_bank.dto;

import lombok.Data;

@Data
public class AccountResponse {
    private Long accountId;
    private String accountType;
    private Double balance;
    private String currency;
    private Long userId;
}
