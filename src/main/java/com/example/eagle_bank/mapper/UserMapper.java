package com.example.eagle_bank.mapper;

import com.example.eagle_bank.dto.UserRequest;
import com.example.eagle_bank.dto.UserResponse;
import com.example.eagle_bank.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toDto(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setName(user.getName());
        userResponse.setSurname(user.getSurname());
        userResponse.setEmail(user.getEmail());
        return userResponse;
    }

    public User toEntity(UserRequest userRequest, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return user;
    }
}
