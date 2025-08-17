package com.example.eagle_bank.dto;

import lombok.Data;

@Data
public class TransactionResponse {
    private String id;
    private Double amount;
    private String currency;
    private String type;
    private String reference;
    private String userId;
    private String createdTimestamp;
}
