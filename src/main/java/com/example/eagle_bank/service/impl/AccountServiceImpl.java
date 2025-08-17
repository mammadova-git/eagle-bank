package com.example.eagle_bank.service.impl;

import com.example.eagle_bank.exception.AccountAlreadyExistsException;
import com.example.eagle_bank.mapper.AccountMapper;
import com.example.eagle_bank.dto.CreateBankAccountRequest;
import com.example.eagle_bank.dto.BankAccountResponse;
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
    public BankAccountResponse createAccount(CreateBankAccountRequest createBankAccountRequest, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        if (accountRepository.existsByUserIdAndNameAndAccountType(
                userId, createBankAccountRequest.getName(), createBankAccountRequest.getAccountType())) {
            throw new AccountAlreadyExistsException("Account already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        Account account = accountMapper.toEntity(createBankAccountRequest, user);
        Account savedAccount = accountRepository.save(account);

        return accountMapper.toDto(savedAccount);
    }

    @Override
    public List<BankAccountResponse> getAccounts(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        List<Account> accounts = accountRepository.findAllByUserId(userId);

        return accounts.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BankAccountResponse getAccount(Long accountId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found", HttpStatus.NOT_FOUND));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccountAccessDeniedException("You are not authorized to access this account.", HttpStatus.FORBIDDEN);
        }

        return accountMapper.toDto(account);
    }
}
