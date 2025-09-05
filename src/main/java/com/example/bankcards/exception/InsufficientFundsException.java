package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InsufficientFundsException extends ResponseStatusException {
    public static final String MSG = "Card id: %d, insufficient funds";

    public InsufficientFundsException(Long id) {
        super(HttpStatus.BAD_REQUEST, MSG.formatted(id));
    }
}