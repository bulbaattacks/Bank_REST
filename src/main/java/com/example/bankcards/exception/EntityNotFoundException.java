package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotFoundException extends ResponseStatusException {
    public static final String ENTITY_NOT_FOUND = "Entity id: %d, not found";

    public EntityNotFoundException(Long id) {
        super(HttpStatus.BAD_REQUEST, ENTITY_NOT_FOUND.formatted(id));
    }
}
