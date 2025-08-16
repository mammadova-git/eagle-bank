package com.example.eagle_bank.mapper;

import com.example.eagle_bank.dto.AccountRequest;
import com.example.eagle_bank.dto.AccountResponse;
import com.example.eagle_bank.entity.Account;
import com.example.eagle_bank.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toDto(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getId());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        response.setUserId(account.getUser().getId());
        return response;
    }

    public Account toEntity(AccountRequest accountRequest, User user) {
        Account account = new Account();
        account.setAccountType(accountRequest.getAccountType());
        account.setBalance(accountRequest.getInitialDeposit());
        account.setCurrency(accountRequest.getCurrency());
        account.setUser(user);
        return account;
    }
}