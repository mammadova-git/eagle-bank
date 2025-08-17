package com.example.eagle_bank.controller;

import com.example.eagle_bank.dto.Address;
import com.example.eagle_bank.dto.CreateUserRequest;
import com.example.eagle_bank.dto.UserResponse;
import com.example.eagle_bank.exception.UserAccessDeniedException;
import com.example.eagle_bank.exception.UserAlreadyExistsException;
import com.example.eagle_bank.exception.UserDeletionConflictException;
import com.example.eagle_bank.exception.UserNotFoundException;
import com.example.eagle_bank.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_shouldReturnUserResponse() {
        CreateUserRequest request = buildCreateUserRequest();
        UserResponse expected = buildUserResponse();

        when(userService.createUser(request)).thenReturn(expected);

        UserResponse actual = userController.createUser(request);

        assertEquals(expected, actual);
        verify(userService).createUser(request);
    }

    @Test
    void createUser_whenUserNotFound_shouldThrowsUserAlreadyExistsException() {
        CreateUserRequest request = buildCreateUserRequest();

        when(userService.createUser(request))
                .thenThrow(new UserAlreadyExistsException("User with this email already exists", HttpStatus.CONFLICT));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userController.createUser(request);
        });

        assertEquals("User with this email already exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void getUser_shouldReturnUserResponse() {
        Long userId = 1L;
        Authentication auth = mock(Authentication.class);
        UserResponse expected = buildUserResponse();

        when(userService.getUser(userId, auth)).thenReturn(expected);

        UserResponse actual = userController.getUser(userId, auth);

        assertEquals(expected, actual);
        verify(userService).getUser(userId, auth);
    }

    @Test
    void getUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        Long userId = 99L;
        Authentication auth = mock(Authentication.class);

        when(userService.getUser(userId, auth))
                .thenThrow(new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userController.getUser(userId, auth);
        });

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getUser_whenAccessDenied_shouldThrowUserAccessDeniedException() {
        Long userId = 1L;
        Authentication auth = mock(Authentication.class);

        when(userService.getUser(userId, auth))
                .thenThrow(new UserAccessDeniedException("You are not authorized to access this user's data.", HttpStatus.FORBIDDEN));

        UserAccessDeniedException exception = assertThrows(UserAccessDeniedException.class, () -> {
            userController.getUser(userId, auth);
        });

        assertEquals("You are not authorized to access this user's data.", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void updateUser_shouldReturnUpdatedUserResponse() {
        Long userId = 1L;
        CreateUserRequest request = buildCreateUserRequest();
        Authentication auth = mock(Authentication.class);
        UserResponse expected = buildUserResponse();

        when(userService.updateUser(userId, request, auth)).thenReturn(expected);

        UserResponse actual = userController.updateUser(userId, request, auth);

        assertEquals(expected, actual);
        verify(userService).updateUser(userId, request, auth);
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        Long userId = 99L;
        CreateUserRequest request = buildCreateUserRequest();
        Authentication auth = mock(Authentication.class);

        when(userService.updateUser(userId, request, auth))
                .thenThrow(new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userController.updateUser(userId, request, auth);
        });

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateUser_whenAccessDenied_shouldThrowUserAccessDeniedException() {
        Long userId = 1L;
        CreateUserRequest request = buildCreateUserRequest();
        Authentication auth = mock(Authentication.class);

        when(userService.updateUser(userId, request, auth))
                .thenThrow(new UserAccessDeniedException("You are not authorized to access this user's data.", HttpStatus.FORBIDDEN));

        UserAccessDeniedException exception = assertThrows(UserAccessDeniedException.class, () -> {
            userController.updateUser(userId, request, auth);
        });

        assertEquals("You are not authorized to access this user's data.", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void deleteUser_shouldInvokeServiceDelete() {
        Long userId = 1L;
        Authentication auth = mock(Authentication.class);

        doNothing().when(userService).deleteUser(userId, auth);

        userController.deleteUser(userId, auth);

        verify(userService).deleteUser(userId, auth);
    }

    @Test
    void deleteUser_whenUserHasBankAccount_shouldThrowUserDeletionConflictException() {
        Long userId = 1L;
        Authentication auth = mock(Authentication.class);

        doThrow(new UserDeletionConflictException("Cannot delete user with associated bank account.", HttpStatus.CONFLICT))
                .when(userService).deleteUser(userId, auth);

        UserDeletionConflictException exception = assertThrows(UserDeletionConflictException.class, () -> {
            userController.deleteUser(userId, auth);
        });

        assertEquals("Cannot delete user with associated bank account.", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    private Address buildAddress() {
        return Address.builder()
                .line1("123 Main St")
                .line2("Apt 4B")
                .line3("Building C")
                .town("Leigh")
                .county("Greater Manchester")
                .postcode("WN7 1AA")
                .build();
    }

    private CreateUserRequest buildCreateUserRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Alice");
        request.setAddress(buildAddress());
        request.setPhoneNumber("+447123456789");
        request.setEmail("alice@example.com");
        request.setPassword("securePassword123");
        return request;
    }

    private UserResponse buildUserResponse() {
        UserResponse response = new UserResponse();
        response.setId("1");
        response.setName("Alice");
        response.setAddress(buildAddress());
        response.setPhoneNumber("+447123456789");
        response.setEmail("alice@example.com");
        response.setCreatedTimestamp("2025-08-17T19:00:00");
        response.setUpdatedTimestamp("2025-08-17T19:30:00");
        return response;
    }
}
