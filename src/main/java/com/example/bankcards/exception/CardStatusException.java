package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CardStatusException extends ResponseStatusException {
    public static final String MSG = "Card id: %d, status not active";

    public CardStatusException(Long id) {
        super(HttpStatus.BAD_REQUEST, MSG.formatted(id));
    }
}