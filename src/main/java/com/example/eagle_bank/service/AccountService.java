package com.example.eagle_bank.service;

import com.example.eagle_bank.dto.AccountRequest;
import com.example.eagle_bank.dto.AccountResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest accountRequest, Authentication authentication);

    List<AccountResponse> getAccounts(Authentication authentication);

    AccountResponse getAccount(Long accountId, Authentication authentication);
}
