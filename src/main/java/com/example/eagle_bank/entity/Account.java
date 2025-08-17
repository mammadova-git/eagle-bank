package com.example.eagle_bank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "account")
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private String sortCode;
    private String name;
    private String accountType;
    private Double balance;
    private String currency;
    private String createdTimestamp;
    private String updatedTimestamp;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
