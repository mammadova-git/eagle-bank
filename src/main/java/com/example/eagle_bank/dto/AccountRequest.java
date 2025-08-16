package com.example.eagle_bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountRequest {
    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "SAVINGS|CHECKING|FIXED_DEPOSIT", message = "Invalid account type")
    private String accountType;

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Initial deposit must be greater than zero")
    private Double initialDeposit;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "GBP|USD|EUR", message = "Unsupported currency")
    private String currency;
}
