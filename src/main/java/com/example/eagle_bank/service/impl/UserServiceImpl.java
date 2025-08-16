package com.example.eagle_bank.service.impl;

import com.example.eagle_bank.mapper.UserMapper;
import com.example.eagle_bank.dto.UserRequest;
import com.example.eagle_bank.dto.UserResponse;
import com.example.eagle_bank.entity.User;
import com.example.eagle_bank.exception.UserAccessDeniedException;
import com.example.eagle_bank.exception.UserDeletionConflictException;
import com.example.eagle_bank.exception.UserNotFoundException;
import com.example.eagle_bank.repository.AccountRepository;
import com.example.eagle_bank.repository.UserRepository;
import com.example.eagle_bank.service.UserService;
import com.example.eagle_bank.authentication.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public void createUser(UserRequest userRequest) {
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(encodedPassword);
        User user = userMapper.toEntity(userRequest, passwordEncoder);
        userRepository.save(user);
    }

    @Override
    public UserResponse getUser(Long userId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND, userId));

        if (!principal.getUserId().equals(userId)) {
            throw new UserAccessDeniedException("You are not authorized to access this user's data.", HttpStatus.FORBIDDEN, userId
            );
        }

        return userMapper.toDto(user);
    }

    @Override
    public UserResponse updateUser(Long userId, UserRequest userRequest, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND, userId));

        if (!principal.getUserId().equals(userId)) {
            throw new UserAccessDeniedException("You are not authorized to access this user's data.", HttpStatus.FORBIDDEN, userId);
        }

        updateUserDetails(userRequest, existingUser);
        return userMapper.toDto(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND, userId));

        if (!principal.getUserId().equals(userId)) {
            throw new UserAccessDeniedException("You are not authorized to delete this user.", HttpStatus.FORBIDDEN, userId);
        }

        boolean hasBankAccount = accountRepository.existsByUserId(userId);
        if (hasBankAccount) {
            throw new UserDeletionConflictException("Cannot delete user with associated bank account.", HttpStatus.CONFLICT, userId);
        }

        userRepository.delete(user);
    }

    private void updateUserDetails(UserRequest userRequest, User existingUser) {
        if (userRequest.getName() != null) {
            existingUser.setName(userRequest.getName());
        }
        if (userRequest.getSurname() != null) {
            existingUser.setSurname(userRequest.getSurname());
        }
        if (userRequest.getEmail() != null) {
            existingUser.setEmail(userRequest.getEmail());
        }
        if (userRequest.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        userRepository.save(existingUser);
    }
}
