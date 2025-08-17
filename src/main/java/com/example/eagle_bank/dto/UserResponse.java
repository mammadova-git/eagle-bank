package com.example.eagle_bank.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String name;
    private Address address;
    private String phoneNumber;
    private String email;
    private String createdTimestamp;
    private String updatedTimestamp;
}
