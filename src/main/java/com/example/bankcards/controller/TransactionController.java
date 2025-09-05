package com.example.bankcards.controller;

import com.example.bankcards.dto.DepositDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/transaction_history")
    public List<TransactionDto> getTransactionHistory(Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        if (authenticatedUser.getRole() == User.Role.ADMIN) {
            return service.getAllTransactionHistory();
        } else {
            return service.getTransactionHistoryByUserId(authenticatedUser.getId());
        }
    }
}
