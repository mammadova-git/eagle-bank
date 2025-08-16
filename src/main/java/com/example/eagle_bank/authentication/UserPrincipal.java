package com.example.eagle_bank.authentication;

import lombok.Data;

@Data
public class UserPrincipal {
    private final Long userId;
    private final String email;
}
