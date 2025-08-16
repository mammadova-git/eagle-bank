package com.example.eagle_bank.mapper;

import com.example.eagle_bank.dto.TransactionRequest;
import com.example.eagle_bank.entity.Account;
import com.example.eagle_bank.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(Account account, TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setType(transactionRequest.getType());
        return transaction;
    }
}
