package com.example.eagle_bank.service;

import com.example.eagle_bank.dto.UserRequest;
import com.example.eagle_bank.dto.UserResponse;
import org.springframework.security.core.Authentication;

public interface UserService {
    void createUser(UserRequest userRequest);

    UserResponse getUser(Long userId, Authentication authentication);

    UserResponse updateUser(Long userId, UserRequest userRequest, Authentication authentication);

    void deleteUser(Long userId, Authentication authentication);
}
