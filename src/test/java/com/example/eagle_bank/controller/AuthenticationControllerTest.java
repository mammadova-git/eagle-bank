package com.example.eagle_bank.controller;

import com.example.eagle_bank.dto.AuthRequest;
import com.example.eagle_bank.dto.AuthResponse;
import com.example.eagle_bank.exception.AuthenticationFailedException;
import com.example.eagle_bank.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void authenticate_whenValidCredentials_shouldReturnJwtToken() {
        AuthRequest request = buildAuthRequest();
        AuthResponse expected = buildAuthResponse();

        when(authenticationService.authenticate(request)).thenReturn(expected);

        ResponseEntity<AuthResponse> response = authenticationController.authenticate(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected.getToken(), response.getBody().getToken());
        verify(authenticationService).authenticate(request);
    }

    @Test
    void authenticate_whenInvalidCredentials_shouldThrowAuthenticationFailedException() {
        AuthRequest request = buildAuthRequest();

        when(authenticationService.authenticate(request))
                .thenThrow(new AuthenticationFailedException("Invalid email or password"));

        AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class, () -> {
            authenticationController.authenticate(request);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }

    private AuthRequest buildAuthRequest() {
        AuthRequest request = new AuthRequest();
        request.setEmail("user@example.com");
        request.setPassword("securePassword123");
        return request;
    }

    private AuthResponse buildAuthResponse() {
        return new AuthResponse("mocked-jwt-token");
    }
}
