package com.example.eagle_bank.service;

import com.example.eagle_bank.dto.CreateBankAccountRequest;
import com.example.eagle_bank.dto.BankAccountResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AccountService {
    BankAccountResponse createAccount(CreateBankAccountRequest createBankAccountRequest, Authentication authentication);

    List<BankAccountResponse> getAccounts(Authentication authentication);

    BankAccountResponse getAccount(Long accountId, Authentication authentication);
}
