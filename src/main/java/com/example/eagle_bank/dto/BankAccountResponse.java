package com.example.eagle_bank.dto;

import lombok.Data;

@Data
public class BankAccountResponse {
    private String accountNumber;
    private String sortCode;
    private String name;
    private String accountType;
    private Double balance;
    private String currency;
    private String createdTimestamp;
    private String updatedTimestamp;
}
