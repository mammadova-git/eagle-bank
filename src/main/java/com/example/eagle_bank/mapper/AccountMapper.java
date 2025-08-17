package com.example.eagle_bank.mapper;

import com.example.eagle_bank.dto.CreateBankAccountRequest;
import com.example.eagle_bank.dto.BankAccountResponse;
import com.example.eagle_bank.entity.Account;
import com.example.eagle_bank.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class AccountMapper {

    public BankAccountResponse toDto(Account account) {
        BankAccountResponse response = new BankAccountResponse();
        response.setAccountNumber(String.valueOf(account.getId()));
        response.setSortCode(String.valueOf(account.getSortCode()));
        response.setName(account.getName());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        response.setCreatedTimestamp(account.getCreatedTimestamp());
        response.setUpdatedTimestamp(account.getUpdatedTimestamp());

        return response;
    }

    public Account toEntity(CreateBankAccountRequest createBankAccountRequest, User user) {
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setSortCode(generateSortCode());
        account.setName(createBankAccountRequest.getName());
        account.setAccountType(createBankAccountRequest.getAccountType());
        account.setBalance((double) 0); // initial balance is 0 when account is created
        account.setCurrency("GBP"); // assuming it's always UK bank account
        account.setCreatedTimestamp(String.valueOf(LocalDateTime.now()));
        account.setUpdatedTimestamp(String.valueOf(LocalDateTime.now()));
        account.setUser(user);
        return account;
    }

    private String generateSortCode() {
        Random random = new Random();
        int part1 = random.nextInt(100);
        int part2 = random.nextInt(100);
        int part3 = random.nextInt(100);

        return String.format("%02d-%02d-%02d", part1, part2, part3);
    }

    private String generateAccountNumber() {
        Random random = new Random();

        // ^01\d{6}$ regex
        StringBuilder sb = new StringBuilder("01");
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}