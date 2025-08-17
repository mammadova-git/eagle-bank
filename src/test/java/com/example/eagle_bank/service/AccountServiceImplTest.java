package com.example.eagle_bank.service;

import com.example.eagle_bank.authentication.UserPrincipal;
import com.example.eagle_bank.dto.BankAccountResponse;
import com.example.eagle_bank.dto.CreateBankAccountRequest;
import com.example.eagle_bank.entity.Account;
import com.example.eagle_bank.entity.User;
import com.example.eagle_bank.exception.AccountAccessDeniedException;
import com.example.eagle_bank.exception.AccountAlreadyExistsException;
import com.example.eagle_bank.exception.AccountNotFoundException;
import com.example.eagle_bank.exception.UserNotFoundException;
import com.example.eagle_bank.mapper.AccountMapper;
import com.example.eagle_bank.repository.AccountRepository;
import com.example.eagle_bank.repository.UserRepository;
import com.example.eagle_bank.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock private AccountRepository accountRepository;
    @Mock private UserRepository userRepository;
    @Mock private AccountMapper accountMapper;

    @InjectMocks private AccountServiceImpl accountService;

    @Test
    void createAccount_shouldCreateNewAccount() {
        Long userId = 1L;
        Authentication auth = mockAuthWithUserId(userId);
        CreateBankAccountRequest request = buildCreateRequest();
        User user = buildUser(userId);
        Account account = buildAccount(userId);
        BankAccountResponse expected = buildResponse();

        when(accountRepository.existsByUserIdAndNameAndAccountType(userId, request.getName(), request.getAccountType()))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountMapper.toEntity(request, user)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(expected);

        BankAccountResponse actual = accountService.createAccount(request, auth);

        assertEquals(expected, actual);
        verify(accountRepository).save(account);
    }

    @Test
    void createAccount_whenAccountExists_shouldThrowAccountAlreadyExistsException() {
        Long userId = 1L;
        Authentication auth = mockAuthWithUserId(userId);
        CreateBankAccountRequest request = buildCreateRequest();

        when(accountRepository.existsByUserIdAndNameAndAccountType(userId, request.getName(), request.getAccountType()))
                .thenReturn(true);

        AccountAlreadyExistsException ex = assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.createAccount(request, auth);
        });

        assertEquals("Account already exists", ex.getMessage());
    }

    @Test
    void createAccount_whenUserNotFound_shouldThrowUserNotFoundException() {
        Long userId = 1L;
        Authentication auth = mockAuthWithUserId(userId);
        CreateBankAccountRequest request = buildCreateRequest();

        when(accountRepository.existsByUserIdAndNameAndAccountType(userId, request.getName(), request.getAccountType()))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            accountService.createAccount(request, auth);
        });

        assertEquals("User not found", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void getAccounts_shouldReturnListOfResponses() {
        Long userId = 1L;
        Authentication auth = mockAuthWithUserId(userId);
        Account account = buildAccount(userId);
        BankAccountResponse response = buildResponse();

        when(accountRepository.findAllByUserId(userId)).thenReturn(List.of(account));
        when(accountMapper.toDto(account)).thenReturn(response);

        List<BankAccountResponse> result = accountService.getAccounts(auth);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getAccounts_whenNoAccounts_shouldReturnEmptyList() {
        Long userId = 1L;
        Authentication auth = mockAuthWithUserId(userId);

        when(accountRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<BankAccountResponse> result = accountService.getAccounts(auth);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAccount_shouldReturnAccountResponse() {
        Long userId = 1L;
        Long accountId = 100L;
        Authentication auth = mockAuthWithUserId(userId);
        Account account = buildAccount(userId);
        BankAccountResponse response = buildResponse();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(response);

        BankAccountResponse result = accountService.getAccount(accountId, auth);

        assertEquals(response, result);
    }

    @Test
    void getAccount_whenNotFound_shouldThrowAccountNotFoundException() {
        Long accountId = 100L;
        Authentication auth = mockAuthWithUserId(1L);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        AccountNotFoundException ex = assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccount(accountId, auth);
        });

        assertEquals("Account not found", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void getAccount_whenAccessDenied_shouldThrowAccountAccessDeniedException() {
        Long accountId = 100L;
        Long ownerId = 1L;
        Long requesterId = 99L;
        Authentication auth = mockAuthWithUserId(requesterId);
        Account account = buildAccount(ownerId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountAccessDeniedException ex = assertThrows(AccountAccessDeniedException.class, () -> {
            accountService.getAccount(accountId, auth);
        });

        assertEquals("You are not authorized to access this account.", ex.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    private Authentication mockAuthWithUserId(Long userId) {
        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getUserId()).thenReturn(userId);
        when(auth.getPrincipal()).thenReturn(principal);
        return auth;
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
        response.setCreatedTimestamp("2023-01-01T10:00:00");
        response.setUpdatedTimestamp("2023-01-01T10:00:00");
        return response;
    }

    private Account buildAccount(Long userId) {
        Account account = new Account();
        User user = new User();
        user.setId(userId);
        account.setUser(user);
        account.setName("Savings Account");
        account.setAccountType("SAVINGS");
        return account;
    }

    private User buildUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        return user;
    }
}
