package com.example.eagle_bank.mapper;

import com.example.eagle_bank.dto.Address;
import com.example.eagle_bank.dto.CreateUserRequest;
import com.example.eagle_bank.dto.UserResponse;
import com.example.eagle_bank.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {
    public UserResponse toDto(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(String.valueOf(user.getId()));
        userResponse.setName(user.getName());
        userResponse.setAddress(Address.builder()
                .line1(user.getLine1())
                .line2(user.getLine2())
                .line3(user.getLine3())
                .town(user.getTown())
                .county(user.getCounty())
                .postcode(user.getPostcode())
                .build());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setEmail(user.getEmail());
        userResponse.setCreatedTimestamp(user.getCreatedTimestamp());
        userResponse.setUpdatedTimestamp(user.getUpdatedTimestamp());
        return userResponse;
    }

    public User toEntity(CreateUserRequest createUserRequest, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setName(createUserRequest.getName());
        user.setLine1(createUserRequest.getAddress().getLine1());
        user.setLine2(createUserRequest.getAddress().getLine2());
        user.setLine3(createUserRequest.getAddress().getLine3());
        user.setTown(createUserRequest.getAddress().getTown());
        user.setCounty(createUserRequest.getAddress().getCounty());
        user.setPostcode(createUserRequest.getAddress().getPostcode());
        user.setPhoneNumber(createUserRequest.getPhoneNumber());
        user.setEmail(createUserRequest.getEmail());
        user.setCreatedTimestamp(String.valueOf(LocalDateTime.now()));
        user.setUpdatedTimestamp(String.valueOf(LocalDateTime.now()));
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        return user;
    }
}
