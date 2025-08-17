package com.example.eagle_bank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBankAccountRequest {
    @NotBlank(message = "Account name is required")
    private String name;

    @NotBlank(message = "Account type is required")
    private String accountType;
}
