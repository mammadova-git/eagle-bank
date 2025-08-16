package com.example.eagle_bank.controller;

import com.example.eagle_bank.dto.UserRequest;
import com.example.eagle_bank.dto.UserResponse;
import com.example.eagle_bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public void createUser(@RequestBody UserRequest userRequest){
        userService.createUser(userRequest);
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId, Authentication authentication) {
        return userService.getUser(userId, authentication);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody UserRequest userRequest, Authentication authentication) {
        return userService.updateUser(userId, userRequest, authentication);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId, Authentication authentication) {
        userService.deleteUser(userId, authentication);
    }
}
