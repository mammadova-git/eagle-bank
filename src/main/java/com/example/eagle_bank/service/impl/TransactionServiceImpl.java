package com.example.eagle_bank.service.impl;

import com.example.eagle_bank.mapper.TransactionMapper;
import com.example.eagle_bank.dto.TransactionRequest;
import com.example.eagle_bank.entity.Account;
import com.example.eagle_bank.entity.Transaction;
import com.example.eagle_bank.exception.AccountAccessDeniedException;
import com.example.eagle_bank.exception.AccountNotFoundException;
import com.example.eagle_bank.repository.AccountRepository;
import com.example.eagle_bank.repository.TransactionRepository;
import com.example.eagle_bank.service.TransactionService;
import com.example.eagle_bank.authentication.UserPrincipal;
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
    public void createTransaction(Long accountId, TransactionRequest transactionRequest, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found", HttpStatus.NOT_FOUND, accountId));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccountAccessDeniedException("You are not authorized to access this account.", HttpStatus.FORBIDDEN, accountId);
        }

        if (!transactionRequest.getType().equalsIgnoreCase("deposit")) {
            throw new IllegalArgumentException("Unsupported transaction type");
        }

        updateAccountBalance(transactionRequest, account);

        saveTransaction(transactionRequest, account);
    }

    private void updateAccountBalance(TransactionRequest transactionRequest, Account account) {
        account.setBalance(account.getBalance() + transactionRequest.getAmount());
        accountRepository.save(account);
    }

    private void saveTransaction(TransactionRequest transactionRequest, Account account) {
        Transaction transaction = transactionMapper.toEntity(account, transactionRequest);
        transactionRepository.save(transaction);
    }
}
