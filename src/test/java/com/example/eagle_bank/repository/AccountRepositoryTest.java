package com.example.eagle_bank.repository;

import com.example.eagle_bank.entity.Account;
import com.example.eagle_bank.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByUserId_shouldReturnAccounts() {
        User user = createUser(1L);
        createAccount(user, "ACC123", "Savings", "Personal");
        createAccount(user, "ACC456", "Current", "Business");

        List<Account> accounts = accountRepository.findAllByUserId(user.getId());

        assertEquals(2, accounts.size());
    }

    @Test
    void existsByUserId_shouldReturnTrueIfExists() {
        User user = createUser(2L);
        createAccount(user, "ACC789", "Joint", "Personal");

        boolean exists = accountRepository.existsByUserId(user.getId());

        assertTrue(exists);
    }

    @Test
    void existsByUserId_shouldReturnFalseIfNotExists() {
        boolean exists = accountRepository.existsByUserId(999L);

        assertFalse(exists);
    }

    @Test
    void existsByUserIdAndNameAndAccountType_shouldReturnTrueIfMatchExists() {
        User user = createUser(3L);
        createAccount(user, "ACC321", "Holiday Fund", "Savings");

        boolean exists = accountRepository.existsByUserIdAndNameAndAccountType(
                user.getId(), "Holiday Fund", "Savings");

        assertTrue(exists);
    }

    @Test
    void existsByUserIdAndNameAndAccountType_shouldReturnFalseIfNoMatch() {
        User user = createUser(4L);
        createAccount(user, "ACC654", "Emergency", "Savings");

        boolean exists = accountRepository.existsByUserIdAndNameAndAccountType(
                user.getId(), "Emergency", "Business");

        assertFalse(exists);
    }

    @Test
    void findByAccountNumber_shouldReturnAccount() {
        User user = createUser(5L);
        Account account = createAccount(user, "ACC999", "Main", "Personal");

        Optional<Account> result = accountRepository.findByAccountNumber("ACC999");

        assertTrue(result.isPresent());
        assertEquals("Main", result.get().getName());
    }

    @Test
    void findByAccountNumber_shouldReturnEmptyIfNotFound() {
        Optional<Account> result = accountRepository.findByAccountNumber("NONEXISTENT");

        assertTrue(result.isEmpty());
    }

    private User createUser(Long id) {
        User user = new User();
        user.setName("Bob");
        user.setEmail("bob@example.com");
        user.setPhoneNumber("07123456789");
        user.setLine1("456 High St");
        user.setTown("Leigh");
        user.setCounty("Greater Manchester");
        user.setPostcode("WN7 2BB");
        user.setPassword("securePassword");
        user.setCreatedTimestamp("2023-01-01T10:00:00");
        user.setUpdatedTimestamp("2023-01-01T10:00:00");
        return userRepository.save(user);
    }

    private Account createAccount(User user, String accountNumber, String name, String type) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setSortCode("12-34-56");
        account.setName(name);
        account.setAccountType(type);
        account.setBalance(1000.0);
        account.setCurrency("GBP");
        account.setCreatedTimestamp("2023-01-01T10:00:00");
        account.setUpdatedTimestamp("2023-01-01T10:00:00");
        account.setUser(user);
        return accountRepository.save(account);
    }
}
