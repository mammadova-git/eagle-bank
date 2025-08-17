package com.example.eagle_bank.service.impl;

import com.example.eagle_bank.exception.UserAlreadyExistsException;
import com.example.eagle_bank.mapper.UserMapper;
import com.example.eagle_bank.dto.CreateUserRequest;
import com.example.eagle_bank.dto.UserResponse;
import com.example.eagle_bank.entity.User;
import com.example.eagle_bank.exception.UserAccessDeniedException;
import com.example.eagle_bank.exception.UserDeletionConflictException;
import com.example.eagle_bank.exception.UserNotFoundException;
import com.example.eagle_bank.repository.AccountRepository;
import com.example.eagle_bank.repository.UserRepository;
import com.example.eagle_bank.service.UserService;
import com.example.eagle_bank.authentication.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        if (userRepository.findByEmail(createUserRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists", HttpStatus.CONFLICT);
        }
        User user = userMapper.toEntity(createUserRequest, passwordEncoder);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponse getUser(Long userId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        if (!principal.getUserId().equals(userId)) {
            throw new UserAccessDeniedException("You are not authorized to access this user's data.", HttpStatus.FORBIDDEN);
        }

        return userMapper.toDto(user);
    }

    @Override
    public UserResponse updateUser(Long userId, CreateUserRequest createUserRequest, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        if (!principal.getUserId().equals(userId)) {
            throw new UserAccessDeniedException("You are not authorized to access this user's data.", HttpStatus.FORBIDDEN);
        }

        updateUserDetails(createUserRequest, existingUser);
        return userMapper.toDto(existingUser);
    }

    @Override
    public void deleteUser(Long userId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        if (!principal.getUserId().equals(userId)) {
            throw new UserAccessDeniedException("You are not authorized to delete this user.", HttpStatus.FORBIDDEN);
        }

        boolean hasBankAccount = accountRepository.existsByUserId(userId);
        if (hasBankAccount) {
            throw new UserDeletionConflictException("Cannot delete user with associated bank account.", HttpStatus.CONFLICT);
        }

        userRepository.delete(user);
    }

    private void updateUserDetails(CreateUserRequest createUserRequest, User existingUser) {
        if (createUserRequest.getName() != null) {
            existingUser.setName(createUserRequest.getName());
        }
        if (createUserRequest.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(createUserRequest.getPhoneNumber());
        }
        if (createUserRequest.getEmail() != null) {
            existingUser.setEmail(createUserRequest.getEmail());
        }
        if (createUserRequest.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        }

        if (createUserRequest.getAddress() != null) {
            if (createUserRequest.getAddress().getLine1() != null) {
                existingUser.setLine1(createUserRequest.getAddress().getLine1());
            }
            if (createUserRequest.getAddress().getLine2() != null) {
                existingUser.setLine2(createUserRequest.getAddress().getLine2());
            }
            if (createUserRequest.getAddress().getLine3() != null) {
                existingUser.setLine3(createUserRequest.getAddress().getLine3());
            }
            if (createUserRequest.getAddress().getTown() != null) {
                existingUser.setTown(createUserRequest.getAddress().getTown());
            }
            if (createUserRequest.getAddress().getPostcode() != null) {
                existingUser.setPostcode(createUserRequest.getAddress().getPostcode());
            }
            if (createUserRequest.getAddress().getCounty() != null) {
                existingUser.setCounty(createUserRequest.getAddress().getCounty());
            }
        }

        existingUser.setUpdatedTimestamp(String.valueOf(LocalDateTime.now()));

        userRepository.save(existingUser);
    }
}
