package com.example.eagle_bank.service;

import com.example.eagle_bank.authentication.UserPrincipal;
import com.example.eagle_bank.dto.CreateTransactionRequest;
import com.example.eagle_bank.dto.TransactionResponse;
import com.example.eagle_bank.entity.Account;
import com.example.eagle_bank.entity.Transaction;
import com.example.eagle_bank.entity.User;
import com.example.eagle_bank.exception.AccountAccessDeniedException;
import com.example.eagle_bank.exception.AccountNotFoundException;
import com.example.eagle_bank.mapper.TransactionMapper;
import com.example.eagle_bank.repository.AccountRepository;
import com.example.eagle_bank.repository.TransactionRepository;
import com.example.eagle_bank.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private TransactionMapper transactionMapper;

    @InjectMocks private TransactionServiceImpl transactionService;

    @Test
    void createTransaction_shouldCreateDepositTransaction() {
        String accountNumber = "ACC123";
        Long userId = 1L;
        double initialBalance = 500.0;
        Authentication auth = mockAuthWithUserId(userId);
        CreateTransactionRequest request = buildCreateRequest("deposit");
        Account account = buildAccount(accountNumber, userId, initialBalance);
        Transaction transaction = buildTransaction(account, request);
        TransactionResponse expected = buildResponse(request, userId);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toEntity(account, request)).thenReturn(transaction);
        when(transactionMapper.toDto(transaction)).thenReturn(expected);

        TransactionResponse actual = transactionService.createTransaction(accountNumber, request, auth);

        assertEquals(expected, actual);
        verify(accountRepository).save(account);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void createTransaction_whenAccountNotFound_shouldThrowAccountNotFoundException() {
        String accountNumber = "ACC123";
        Long userId = 1L;
        Authentication auth = mockAuthWithUserId(userId);
        CreateTransactionRequest request = buildCreateRequest("deposit");

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        AccountNotFoundException ex = assertThrows(AccountNotFoundException.class, () -> {
            transactionService.createTransaction(accountNumber, request, auth);
        });

        assertEquals("Account not found", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void createTransaction_whenAccessDenied_shouldThrowAccountAccessDeniedException() {
        String accountNumber = "ACC123";
        Long ownerId = 1L;
        Long requesterId = 99L;
        Authentication auth = mockAuthWithUserId(requesterId);
        CreateTransactionRequest request = buildCreateRequest("deposit");
        Account account = buildAccount(accountNumber, ownerId, 500.0);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        AccountAccessDeniedException ex = assertThrows(AccountAccessDeniedException.class, () -> {
            transactionService.createTransaction(accountNumber, request, auth);
        });

        assertEquals("You are not authorized to access this account.", ex.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void createTransaction_whenUnsupportedType_shouldThrowIllegalArgumentException() {
        String accountNumber = "ACC123";
        Long userId = 1L;
        Authentication auth = mockAuthWithUserId(userId);
        CreateTransactionRequest request = buildCreateRequest("withdrawal"); // unsupported
        Account account = buildAccount(accountNumber, userId, 500.0);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(accountNumber, request, auth);
        });

        assertEquals("Unsupported transaction type", ex.getMessage());
    }

    private Authentication mockAuthWithUserId(Long userId) {
        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getUserId()).thenReturn(userId);
        when(auth.getPrincipal()).thenReturn(principal);
        return auth;
    }

    private CreateTransactionRequest buildCreateRequest(String type) {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(100.0);
        request.setCurrency("GBP");
        request.setType(type);
        request.setReference("Initial deposit");
        return request;
    }

    private Account buildAccount(String accountNumber, Long userId, double balance) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        User user = new User();
        user.setId(userId);
        account.setUser(user);
        return account;
    }

    private Transaction buildTransaction(Account account, CreateTransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setType(request.getType());
        transaction.setReference(request.getReference());
        transaction.setAccount(account);
        return transaction;
    }

    private TransactionResponse buildResponse(CreateTransactionRequest request, Long userId) {
        TransactionResponse response = new TransactionResponse();
        response.setId("tx123");
        response.setAmount(request.getAmount());
        response.setCurrency(request.getCurrency());
        response.setType(request.getType());
        response.setReference(request.getReference());
        response.setUserId(String.valueOf(userId));
        response.setCreatedTimestamp("2023-01-01T10:00:00");
        return response;
    }
}