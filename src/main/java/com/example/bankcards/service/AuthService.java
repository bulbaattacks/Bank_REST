package com.example.bankcards.service;

import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
