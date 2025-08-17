package com.example.eagle_bank.service;

import com.example.eagle_bank.dto.AuthRequest;
import com.example.eagle_bank.dto.AuthResponse;
import com.example.eagle_bank.entity.User;
import com.example.eagle_bank.exception.AuthenticationFailedException;
import com.example.eagle_bank.repository.UserRepository;
import com.example.eagle_bank.authentication.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationFailedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationFailedException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(request.getEmail(), user.getId());
        return new AuthResponse(token);
    }
}
