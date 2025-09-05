package com.example.bankcards.controller;

import com.example.bankcards.dto.DepositDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
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
    @PageableAsQueryParam
    public List<TransactionDto> getTransactionHistory(Authentication auth,
                                                      @PageableDefault(value = 20, sort = "date", direction = Sort.Direction.ASC)
                                                      @Parameter(hidden = true) Pageable pageable,
                                                      @RequestParam(required = false) Long amountFilter) {
        var authenticatedUser = ((User) auth.getPrincipal());
        if (authenticatedUser.getRole() == User.Role.ADMIN) {
            return service.getAllTransactionHistory(pageable, amountFilter);
        } else {
            return service.getTransactionHistoryByUserId(authenticatedUser.getId(), pageable, amountFilter);
        }
    }
}
