package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
    public static final String MSG = "User id: %d, not found";

    public UserNotFoundException(Long id) {
        super(HttpStatus.BAD_REQUEST, MSG.formatted(id));
    }
}