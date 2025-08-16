package com.example.eagle_bank.service;

import com.example.eagle_bank.dto.TransactionRequest;
import org.springframework.security.core.Authentication;

public interface TransactionService {
    void createTransaction(Long accountId, TransactionRequest transactionRequest, Authentication authentication);
}
