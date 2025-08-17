package com.example.eagle_bank.service.impl;

import com.example.eagle_bank.dto.TransactionResponse;
import com.example.eagle_bank.mapper.TransactionMapper;
import com.example.eagle_bank.dto.CreateTransactionRequest;
import com.example.eagle_bank.entity.Account;
import com.example.eagle_bank.entity.Transaction;
import com.example.eagle_bank.exception.AccountAccessDeniedException;
import com.example.eagle_bank.exception.AccountNotFoundException;
import com.example.eagle_bank.repository.AccountRepository;
import com.example.eagle_bank.repository.TransactionRepository;
import com.example.eagle_bank.service.TransactionService;
import com.example.eagle_bank.authentication.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionResponse createTransaction(String accountNumber, CreateTransactionRequest createTransactionRequest, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found", HttpStatus.NOT_FOUND));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccountAccessDeniedException("You are not authorized to access this account.", HttpStatus.FORBIDDEN);
        }

        if (!createTransactionRequest.getType().equalsIgnoreCase("deposit")) {
            throw new IllegalArgumentException("Unsupported transaction type");
        }

        updateAccountBalance(createTransactionRequest, account);

        return saveTransaction(createTransactionRequest, account);
    }

    private void updateAccountBalance(CreateTransactionRequest createTransactionRequest, Account account) {
        account.setBalance(account.getBalance() + createTransactionRequest.getAmount());
        accountRepository.save(account);
    }

    private TransactionResponse saveTransaction(CreateTransactionRequest createTransactionRequest, Account account) {
        Transaction transaction = transactionMapper.toEntity(account, createTransactionRequest);

        return transactionMapper.toDto(transactionRepository.save(transaction));
    }
}
