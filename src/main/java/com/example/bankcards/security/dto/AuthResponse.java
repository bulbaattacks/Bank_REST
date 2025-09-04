package com.example.bankcards.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AuthResponse {
    private String token;
    private String refreshToken;
}
