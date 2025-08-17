package com.example.eagle_bank.repository;

import com.example.eagle_bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByUserIdAndNameAndAccountType(Long userId, String name, String accountType);

    Optional<Account> findByAccountNumber(String accountNumber);

}
