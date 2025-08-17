package com.example.eagle_bank.controller;

import com.example.eagle_bank.dto.CreateUserRequest;
import com.example.eagle_bank.dto.UserResponse;
import com.example.eagle_bank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest createUserRequest){
        return userService.createUser(createUserRequest);
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId, Authentication authentication) {
        return userService.getUser(userId, authentication);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody CreateUserRequest createUserRequest, Authentication authentication) {
        return userService.updateUser(userId, createUserRequest, authentication);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId, Authentication authentication) {
        userService.deleteUser(userId, authentication);
    }
}
