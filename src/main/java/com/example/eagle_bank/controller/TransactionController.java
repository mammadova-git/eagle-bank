package com.example.eagle_bank.controller;

import com.example.eagle_bank.dto.CreateTransactionRequest;
import com.example.eagle_bank.dto.TransactionResponse;
import com.example.eagle_bank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{accountNumber}/transactions")
    public TransactionResponse createTransaction(@PathVariable String accountNumber,
                                                 @Valid @RequestBody CreateTransactionRequest createTransactionRequest,
                                                 Authentication authentication) {
        return transactionService.createTransaction(accountNumber, createTransactionRequest, authentication);
    }
}
