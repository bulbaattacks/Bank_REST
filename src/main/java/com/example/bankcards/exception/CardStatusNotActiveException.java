package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CardStatusNotActiveException extends ResponseStatusException {
    public static final String MSG = "Card id: %d, is not active";

    public CardStatusNotActiveException(Long id) {
        super(HttpStatus.BAD_REQUEST, MSG.formatted(id));
    }
}