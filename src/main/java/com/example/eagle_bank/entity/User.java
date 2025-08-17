package com.example.eagle_bank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String line1;
    private String line2;
    private String line3;
    private String town;
    private String county;
    private String postcode;
    private String phoneNumber;
    private String email;
    private String createdTimestamp;
    private String updatedTimestamp;
    private String password;
}
