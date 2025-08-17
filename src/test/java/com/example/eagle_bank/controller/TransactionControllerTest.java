package com.example.eagle_bank.controller;

import com.example.eagle_bank.dto.CreateTransactionRequest;
import com.example.eagle_bank.dto.TransactionResponse;
import com.example.eagle_bank.exception.AccountAccessDeniedException;
import com.example.eagle_bank.exception.AccountNotFoundException;
import com.example.eagle_bank.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void createTransaction_shouldReturnTransactionResponse() {
        String accountNumber = "12345678";
        CreateTransactionRequest request = buildCreateTransactionRequest();
        TransactionResponse expected = buildTransactionResponse();
        Authentication auth = mock(Authentication.class);

        when(transactionService.createTransaction(accountNumber, request, auth)).thenReturn(expected);

        TransactionResponse actual = transactionController.createTransaction(accountNumber, request, auth);

        assertEquals(expected, actual);
        verify(transactionService).createTransaction(accountNumber, request, auth);
    }

    @Test
    void createTransaction_whenAccountNotFound_shouldThrowAccountNotFoundException() {
        String accountNumber = "99999999";
        CreateTransactionRequest request = buildCreateTransactionRequest();
        Authentication auth = mock(Authentication.class);

        when(transactionService.createTransaction(accountNumber, request, auth))
                .thenThrow(new AccountNotFoundException("Account not found", HttpStatus.NOT_FOUND));

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            transactionController.createTransaction(accountNumber, request, auth);
        });

        assertEquals("Account not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createTransaction_whenAccessDenied_shouldThrowAccountAccessDeniedException() {
        String accountNumber = "12345678";
        CreateTransactionRequest request = buildCreateTransactionRequest();
        Authentication auth = mock(Authentication.class);

        when(transactionService.createTransaction(accountNumber, request, auth))
                .thenThrow(new AccountAccessDeniedException("You are not authorized to access this account.", HttpStatus.FORBIDDEN));

        AccountAccessDeniedException exception = assertThrows(AccountAccessDeniedException.class, () -> {
            transactionController.createTransaction(accountNumber, request, auth);
        });

        assertEquals("You are not authorized to access this account.", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void createTransaction_whenUnsupportedType_shouldThrowIllegalArgumentException() {
        String accountNumber = "12345678";
        CreateTransactionRequest request = buildCreateTransactionRequest();
        request.setType("transfer");
        Authentication auth = mock(Authentication.class);

        when(transactionService.createTransaction(accountNumber, request, auth))
                .thenThrow(new IllegalArgumentException("Unsupported transaction type"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionController.createTransaction(accountNumber, request, auth);
        });

        assertEquals("Unsupported transaction type", exception.getMessage());
    }

    private CreateTransactionRequest buildCreateTransactionRequest() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(100.0);
        request.setCurrency("GBP");
        request.setType("deposit");
        request.setReference("Initial deposit");
        return request;
    }

    private TransactionResponse buildTransactionResponse() {
        TransactionResponse response = new TransactionResponse();
        response.setId("txn-001");
        response.setAmount(100.0);
        response.setCurrency("GBP");
        response.setType("deposit");
        response.setReference("Initial deposit");
        response.setUserId("1");
        response.setCreatedTimestamp("2025-08-17T20:00:00");
        return response;
    }
}

