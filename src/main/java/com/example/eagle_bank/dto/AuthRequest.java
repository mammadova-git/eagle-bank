package com.example.eagle_bank.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
