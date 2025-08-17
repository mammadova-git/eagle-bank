package com.example.eagle_bank.service;

import com.example.eagle_bank.dto.CreateTransactionRequest;
import com.example.eagle_bank.dto.TransactionResponse;
import org.springframework.security.core.Authentication;

public interface TransactionService {
    TransactionResponse createTransaction(String accountNumber, CreateTransactionRequest createTransactionRequest, Authentication authentication);
}
