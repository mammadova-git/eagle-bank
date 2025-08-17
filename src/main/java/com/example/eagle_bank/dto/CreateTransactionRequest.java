package com.example.eagle_bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateTransactionRequest {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private Double amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "deposit|withdrawal", message = "Only 'deposit' and 'withdrawal' transactions are supported")
    private String type;

    private String reference;
}

