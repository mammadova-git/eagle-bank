package com.example.eagle_bank.service;

import com.example.eagle_bank.dto.CreateUserRequest;
import com.example.eagle_bank.dto.UserResponse;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserResponse createUser(CreateUserRequest createUserRequest);

    UserResponse getUser(Long userId, Authentication authentication);

    UserResponse updateUser(Long userId, CreateUserRequest createUserRequest, Authentication authentication);

    void deleteUser(Long userId, Authentication authentication);
}
