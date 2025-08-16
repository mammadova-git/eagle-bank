package com.example.eagle_bank.service.impl;

import com.example.eagle_bank.mapper.AccountMapper;
import com.example.eagle_bank.dto.AccountRequest;
import com.example.eagle_bank.dto.AccountResponse;
import com.example.eagle_bank.entity.Account;
import com.example.eagle_bank.entity.User;
import com.example.eagle_bank.exception.AccountAccessDeniedException;
import com.example.eagle_bank.exception.AccountNotFoundException;
import com.example.eagle_bank.exception.UserNotFoundException;
import com.example.eagle_bank.repository.AccountRepository;
import com.example.eagle_bank.repository.UserRepository;
import com.example.eagle_bank.service.AccountService;
import com.example.eagle_bank.authentication.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountResponse createAccount(AccountRequest accountRequest, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND, userId));

        Account account = accountMapper.toEntity(accountRequest, user);
        Account savedAccount = accountRepository.save(account);

        return accountMapper.toDto(savedAccount);
    }

    @Override
    public List<AccountResponse> getAccounts(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        List<Account> accounts = accountRepository.findAllByUserId(userId);

        return accounts.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse getAccount(Long accountId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found", HttpStatus.NOT_FOUND, accountId));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccountAccessDeniedException("You are not authorized to access this account.", HttpStatus.FORBIDDEN, accountId);
        }

        return accountMapper.toDto(account);
    }
}
