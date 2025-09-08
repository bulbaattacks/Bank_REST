package com.example.bankcards.controller;

import com.example.bankcards.dto.DepositDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transactions", description = "Операции с транзакциями и депозитами")
public class TransactionController {

    private final TransactionService service;

    @Operation(
            summary = "Создать транзакцию",
            description = "Создаёт новую транзакцию от имени авторизованного пользователя.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Транзакция успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
                    @ApiResponse(responseCode = "401", description = "Неавторизован")
            }
    )
    @PostMapping("/transaction")
    public void createTransaction(
            @Parameter(description = "Данные транзакции") @Valid @RequestBody TransactionDto dto,
            Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        var ownerId = authenticatedUser.getId();
        service.createTransaction(dto, ownerId);
    }

    @Operation(
            summary = "Создать депозит",
            description = "Вносит депозит на счёт пользователя.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Депозит успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
                    @ApiResponse(responseCode = "401", description = "Неавторизован")
            }
    )
    @PostMapping("/deposit")
    public void createDeposit(
            @Parameter(description = "Данные депозита") @Valid @RequestBody DepositDto dto) {
        service.deposit(dto);
    }

    @Operation(
            summary = "Получить историю транзакций",
            description = "Возвращает историю транзакций. Администратор видит все транзакции, обычный пользователь — только свои.",
            parameters = {
                    @Parameter(name = "amountFilter", description = "Фильтр по сумме транзакции (опционально)"),
                    @Parameter(hidden = true, description = "Параметры пагинации")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "История транзакций",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TransactionDto.class))),
                    @ApiResponse(responseCode = "401", description = "Неавторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён")
            }
    )
    @GetMapping("/transaction_history")
    @PageableAsQueryParam
    public List<TransactionDto> getTransactionHistory(
            Authentication auth,
            @PageableDefault(value = 20, sort = "id", direction = Sort.Direction.ASC)
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
