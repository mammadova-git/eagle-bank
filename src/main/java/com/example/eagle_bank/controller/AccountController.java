package com.example.eagle_bank.controller;

import com.example.eagle_bank.dto.AccountRequest;
import com.example.eagle_bank.dto.AccountResponse;
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
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest accountRequest,
                                                         Authentication authentication
    ) {
        AccountResponse response = accountService.createAccount(accountRequest, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts(Authentication authentication) {
        List<AccountResponse> accounts = accountService.getAccounts(authentication);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId,
                                                          Authentication authentication) {
        AccountResponse accountResponse = accountService.getAccount(accountId, authentication);
        return ResponseEntity.ok(accountResponse);
    }
}
