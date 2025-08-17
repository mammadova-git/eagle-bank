package com.example.eagle_bank.repository;

import com.example.eagle_bank.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser() {
        String email = "alice@example.com";
        User user = buildUser(email);
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        assertEquals("Alice", result.get().getName());
    }

    @Test
    void findByEmail_whenUserDoesNotExist_shouldReturnEmpty() {
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        assertTrue(result.isEmpty());
    }

    private User buildUser(String email) {
        User user = new User();
        user.setName("Alice");
        user.setEmail(email);
        user.setPhoneNumber("+447123456789");
        user.setLine1("123 Main St");
        user.setTown("Leigh");
        user.setCounty("Greater Manchester");
        user.setPostcode("WN7 1AA");
        user.setPassword("encodedPassword");
        user.setCreatedTimestamp("2023-01-01T10:00:00");
        user.setUpdatedTimestamp("2023-01-01T10:00:00");
        return user;
    }
}