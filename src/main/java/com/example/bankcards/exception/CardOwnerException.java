package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CardOwnerException extends ResponseStatusException {
    public static final String MSG = "Card id: %d, does not belong to user id: %d";

    public CardOwnerException(Long cardId, Long userId) {
        super(HttpStatus.BAD_REQUEST, MSG.formatted(cardId, userId));
    }
}