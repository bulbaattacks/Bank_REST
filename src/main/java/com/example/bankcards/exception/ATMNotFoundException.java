package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ATMNotFoundException extends ResponseStatusException {
    public static final String MSG = "ATM not found";

    public ATMNotFoundException() {
        super(HttpStatus.BAD_REQUEST, MSG);
    }
}