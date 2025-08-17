package com.example.eagle_bank.controller;

import com.example.eagle_bank.dto.CreateBankAccountRequest;
import com.example.eagle_bank.dto.BankAccountResponse;
import com.example.eagle_bank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody CreateBankAccountRequest createBankAccountRequest,
                                                             Authentication authentication
    ) {
        BankAccountResponse response = accountService.createAccount(createBankAccountRequest, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> getAccounts(Authentication authentication) {
        List<BankAccountResponse> accounts = accountService.getAccounts(authentication);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<BankAccountResponse> getAccount(@PathVariable Long accountId,
                                                          Authentication authentication) {
        BankAccountResponse bankAccountResponse = accountService.getAccount(accountId, authentication);
        return ResponseEntity.ok(bankAccountResponse);
    }
}
