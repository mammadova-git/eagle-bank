package com.example.eagle_bank.mapper;

import com.example.eagle_bank.dto.CreateTransactionRequest;
import com.example.eagle_bank.dto.TransactionResponse;
import com.example.eagle_bank.entity.Account;
import com.example.eagle_bank.entity.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    public Transaction toEntity(Account account, CreateTransactionRequest createTransactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(createTransactionRequest.getAmount());
        transaction.setType(createTransactionRequest.getType());
        transaction.setCurrency(createTransactionRequest.getCurrency());
        transaction.setReference(createTransactionRequest.getReference());
        transaction.setCreatedTimestamp(String.valueOf(LocalDateTime.now()));
        return transaction;
    }

    public TransactionResponse toDto(Transaction transaction) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setId(String.valueOf(transaction.getId()));
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setType(transaction.getType());
        transactionResponse.setCurrency(transaction.getCurrency());
        transactionResponse.setReference(transaction.getReference());
        transactionResponse.setUserId(String.valueOf(transaction.getAccount().getUser().getId()));
        transactionResponse.setCreatedTimestamp(transaction.getCreatedTimestamp());

        return transactionResponse;
    }
}
