package com.example.eagle_bank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "transaction")
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private Double amount;
    private String currency;
    private String reference;
    private String createdTimestamp;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;
}
