package com.example.bankcards.security.controller;

import com.example.bankcards.security.dto.AuthRequest;
import com.example.bankcards.security.dto.AuthResponse;
import com.example.bankcards.security.service.AuthService;
import com.example.bankcards.security.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Регистрация и аутентификация пользователей")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создаёт нового пользователя в системе и возвращает токен авторизации.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
            }
    )
    @PostMapping("/register")
    public AuthResponse register(
            @Parameter(description = "Данные для регистрации пользователя", required = true)
            @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверяет логин и пароль пользователя и возвращает токен авторизации.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Аутентификация успешна",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
                    @ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
            }
    )
    @PostMapping("/authenticate")
    public AuthResponse authenticate(
            @Parameter(description = "Данные для входа пользователя", required = true)
            @RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }
}
