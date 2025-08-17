package com.example.eagle_bank.controller;

import com.example.eagle_bank.dto.BankAccountResponse;
import com.example.eagle_bank.dto.CreateBankAccountRequest;
import com.example.eagle_bank.exception.AccountAccessDeniedException;
import com.example.eagle_bank.exception.AccountAlreadyExistsException;
import com.example.eagle_bank.exception.AccountNotFoundException;
import com.example.eagle_bank.exception.UserNotFoundException;
import com.example.eagle_bank.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void createAccount_shouldReturnBankAccountResponse() {
        CreateBankAccountRequest request = buildCreateRequest();
        BankAccountResponse expected = buildResponse();
        Authentication auth = mock(Authentication.class);

        when(accountService.createAccount(request, auth)).thenReturn(expected);

        ResponseEntity<BankAccountResponse> response = accountController.createAccount(request, auth);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
        verify(accountService).createAccount(request, auth);
    }

    @Test
    void createAccount_whenAccountAlreadyExists_shouldThrowException() {
        CreateBankAccountRequest request = buildCreateRequest();
        Authentication auth = mock(Authentication.class);

        when(accountService.createAccount(request, auth))
                .thenThrow(new AccountAlreadyExistsException("Account already exists"));

        AccountAlreadyExistsException exception = assertThrows(AccountAlreadyExistsException.class, () -> {
            accountController.createAccount(request, auth);
        });

        assertEquals("Account already exists", exception.getMessage());
    }

    @Test
    void createAccount_whenUserNotFound_shouldThrowUserNotFoundException() {
        CreateBankAccountRequest request = buildCreateRequest();
        Authentication auth = mock(Authentication.class);

        when(accountService.createAccount(request, auth))
                .thenThrow(new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            accountController.createAccount(request, auth);
        });

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getAccounts_shouldReturnListOfAccounts() {
        Authentication auth = mock(Authentication.class);
        BankAccountResponse account1 = buildResponse();
        BankAccountResponse account2 = buildResponse();
        account2.setAccountNumber("87654321");

        when(accountService.getAccounts(auth)).thenReturn(List.of(account1, account2));

        ResponseEntity<List<BankAccountResponse>> response = accountController.getAccounts(auth);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
        verify(accountService).getAccounts(auth);
    }

    @Test
    void getAccount_shouldReturnAccount() {
        Long accountId = 1L;
        Authentication auth = mock(Authentication.class);
        BankAccountResponse expected = buildResponse();

        when(accountService.getAccount(accountId, auth)).thenReturn(expected);

        ResponseEntity<BankAccountResponse> response = accountController.getAccount(accountId, auth);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
        verify(accountService).getAccount(accountId, auth);
    }

    @Test
    void getAccount_whenAccountNotFound_shouldThrowAccountNotFoundException() {
        Long accountId = 99L;
        Authentication auth = mock(Authentication.class);

        when(accountService.getAccount(accountId, auth))
                .thenThrow(new AccountNotFoundException("Account not found", HttpStatus.NOT_FOUND));

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountController.getAccount(accountId, auth);
        });

        assertEquals("Account not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getAccount_whenAccessDenied_shouldThrowAccountAccessDeniedException() {
        Long accountId = 1L;
        Authentication auth = mock(Authentication.class);

        when(accountService.getAccount(accountId, auth))
                .thenThrow(new AccountAccessDeniedException("You are not authorized to access this account.", HttpStatus.FORBIDDEN));

        AccountAccessDeniedException exception = assertThrows(AccountAccessDeniedException.class, () -> {
            accountController.getAccount(accountId, auth);
        });

        assertEquals("You are not authorized to access this account.", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    private CreateBankAccountRequest buildCreateRequest() {
        CreateBankAccountRequest request = new CreateBankAccountRequest();
        request.setName("Savings Account");
        request.setAccountType("SAVINGS");
        return request;
    }

    private BankAccountResponse buildResponse() {
        BankAccountResponse response = new BankAccountResponse();
        response.setAccountNumber("12345678");
        response.setSortCode("12-34-56");
        response.setName("Savings Account");
        response.setAccountType("SAVINGS");
        response.setBalance(1000.0);
        response.setCurrency("GBP");
        response.setCreatedTimestamp("2025-08-17T19:00:00");
        response.setUpdatedTimestamp("2025-08-17T19:30:00");
        return response;
    }
}

