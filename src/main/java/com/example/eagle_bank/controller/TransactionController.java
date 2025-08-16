package com.example.eagle_bank.controller;

import com.example.eagle_bank.dto.TransactionRequest;
import com.example.eagle_bank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<Void> createTransaction(@PathVariable Long accountId,
                                                  @Valid @RequestBody TransactionRequest transactionRequest,
                                                  Authentication authentication) {
        transactionService.createTransaction(accountId, transactionRequest, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
