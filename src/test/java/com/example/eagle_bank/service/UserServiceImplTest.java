package com.example.eagle_bank.service;

import com.example.eagle_bank.authentication.UserPrincipal;
import com.example.eagle_bank.dto.Address;
import com.example.eagle_bank.dto.CreateUserRequest;
import com.example.eagle_bank.dto.UserResponse;
import com.example.eagle_bank.entity.User;
import com.example.eagle_bank.exception.UserAccessDeniedException;
import com.example.eagle_bank.exception.UserAlreadyExistsException;
import com.example.eagle_bank.exception.UserDeletionConflictException;
import com.example.eagle_bank.exception.UserNotFoundException;
import com.example.eagle_bank.mapper.UserMapper;
import com.example.eagle_bank.repository.AccountRepository;
import com.example.eagle_bank.repository.UserRepository;
import com.example.eagle_bank.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserServiceImpl userService;

    @Test
    void createUser_shouldCreateUser() {
        CreateUserRequest request = buildCreateUserRequest();
        User user = buildUser(1L);
        UserResponse expected = buildUserResponse(1L);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request, passwordEncoder)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponse actual = userService.createUser(request);

        assertEquals(expected, actual);
        verify(userRepository).save(user);
    }

    @Test
    void createUser_whenEmailExists_shouldThrowUserAlreadyExistsException() {
        CreateUserRequest request = buildCreateUserRequest();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(request);
        });

        assertEquals("User with this email already exists", ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void getUser_shouldReturnUserResponse() {
        Long userId = 1L;
        User user = buildUser(userId);
        UserResponse expected = buildUserResponse(userId);
        Authentication auth = mockAuthWithUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponse actual = userService.getUser(userId, auth);

        assertEquals(expected, actual);
    }

    @Test
    void getUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        Long userId = 1L;
        Authentication auth = mock(Authentication.class);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(userId, auth);
        });

        assertEquals("User not found", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void getUser_whenAccessDenied_shouldThrowUserAccessDeniedException() {
        Long userId = 1L;
        Authentication auth = mockAuthWithUserId(99L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)));

        UserAccessDeniedException ex = assertThrows(UserAccessDeniedException.class, () -> {
            userService.getUser(userId, auth);
        });

        assertEquals("You are not authorized to access this user's data.", ex.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void updateUser_shouldUpdateAndReturnUserResponse() {
        Long userId = 1L;
        CreateUserRequest request = buildCreateUserRequest();
        User user = buildUser(userId);
        UserResponse expected = buildUserResponse(userId);
        Authentication auth = mockAuthWithUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponse actual = userService.updateUser(userId, request, auth);

        assertEquals(expected, actual);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        Long userId = 1L;
        CreateUserRequest request = buildCreateUserRequest();
        Authentication auth = mock(Authentication.class);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, request, auth);
        });

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void updateUser_whenAccessDenied_shouldThrowUserAccessDeniedException() {
        Long userId = 1L;
        CreateUserRequest request = buildCreateUserRequest();
        Authentication auth = mockAuthWithUserId(99L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)));

        UserAccessDeniedException ex = assertThrows(UserAccessDeniedException.class, () -> {
            userService.updateUser(userId, request, auth);
        });

        assertEquals("You are not authorized to access this user's data.", ex.getMessage());
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        Long userId = 1L;
        User user = buildUser(userId);
        Authentication auth = mockAuthWithUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.existsByUserId(userId)).thenReturn(false);

        userService.deleteUser(userId, auth);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        Long userId = 1L;
        Authentication auth = mock(Authentication.class);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId, auth);
        });

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void deleteUser_whenAccessDenied_shouldThrowUserAccessDeniedException() {
        Long userId = 1L;
        Authentication auth = mockAuthWithUserId(99L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)));

        UserAccessDeniedException ex = assertThrows(UserAccessDeniedException.class, () -> {
            userService.deleteUser(userId, auth);
        });

        assertEquals("You are not authorized to delete this user.", ex.getMessage());
    }

    @Test
    void deleteUser_whenUserHasAccount_shouldThrowUserDeletionConflictException() {
        Long userId = 1L;
        User user = buildUser(userId);
        Authentication auth = mockAuthWithUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.existsByUserId(userId)).thenReturn(true);

        UserDeletionConflictException ex = assertThrows(UserDeletionConflictException.class, () -> {
            userService.deleteUser(userId, auth);
        });

        assertEquals("Cannot delete user with associated bank account.", ex.getMessage());
    }

    private CreateUserRequest buildCreateUserRequest() {
        Address address = Address.builder()
                .line1("123 Main St")
                .town("Leigh")
                .county("Greater Manchester")
                .postcode("WN7 1AA")
                .build();

        CreateUserRequest request = new CreateUserRequest();
        request.setName("Alice");
        request.setAddress(address);
        request.setPhoneNumber("+447123456789");
        request.setEmail("alice@example.com");
        request.setPassword("securePassword123");
        return request;
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("alice@example.com");
        user.setPassword("encodedPassword");
        user.setName("Alice");
        user.setPhoneNumber("+447123456789");
        user.setLine1("123 Main St");
        user.setTown("Leigh");
        user.setCounty("Greater Manchester");
        user.setPostcode("WN7 1AA");
        return user;
    }

    private UserResponse buildUserResponse(Long id) {
        Address address = Address.builder()
                .line1("123 Main St")
                .town("Leigh")
                .county("Greater Manchester")
                .postcode("WN7 1AA")
                .build();

        UserResponse response = new UserResponse();
        response.setId(String.valueOf(id));
        response.setName("Alice");
        response.setEmail("alice@example.com");
        response.setPhoneNumber("+447123456789");
        response.setAddress(address);

        return response;
    }

    private Authentication mockAuthWithUserId(Long userId) {
        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getUserId()).thenReturn(userId);
        when(auth.getPrincipal()).thenReturn(principal);
        return auth;
    }
}
