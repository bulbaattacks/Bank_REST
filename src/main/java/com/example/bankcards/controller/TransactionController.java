package com.example.bankcards.controller;

import com.example.bankcards.dto.DepositDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService service;

    @PostMapping("/transaction")
    public void createTransaction(@Valid @RequestBody TransactionDto dto, Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        var ownerId = authenticatedUser.getId();
        service.createTransaction(dto, ownerId);
    }

    @PostMapping("/deposit")
    public void createDeposit(@Valid @RequestBody DepositDto dto) {
        service.deposit(dto);
    }
}
