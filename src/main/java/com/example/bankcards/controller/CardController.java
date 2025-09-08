package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardStatusException;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cards", description = "Операции с картами пользователей")
public class CardController {

    private final CardService service;

    @Operation(
            summary = "Получить список всех карт",
            description = "Возвращает список карт с возможностью фильтрации по статусу и пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список карт успешно получен",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CardDto.class))),
                    @ApiResponse(responseCode = "401", description = "Неавторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён")
            }
    )
    @GetMapping("/cards")
    @PageableAsQueryParam
    public List<CardDto> getAllCards(
            @PageableDefault(value = 20, sort = "id", direction = Sort.Direction.ASC)
            @Parameter(hidden = true) Pageable pageable,
            @Parameter(description = "Фильтр по статусу карты")
            @RequestParam(required = false) Card.Status statusFilter) {
        return service.getAllCards(pageable, statusFilter);
    }

    @Operation(
            summary = "Получить карты пользователя",
            description = "Доступно администратору или владельцу карт",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список карт пользователя",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CardDto.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён")
            }
    )
    @GetMapping("/cards/{userId}")
    public List<CardDto> getCardsByUserId(
            @Parameter(description = "ID пользователя") @PathVariable("userId") Long userId,
            Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        if (authenticatedUser.getRole() != User.Role.ADMIN && !authenticatedUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return service.getCardsByUserId(userId);
    }

    @Operation(
            summary = "Создать новую карту",
            description = "Создаёт новую карту на основе переданных данных",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта успешно создана",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CardDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные")
            }
    )
    @PostMapping("/cards")
    public CardDto createCard(
            @Parameter(description = "Данные новой карты") @Valid @RequestBody CardDto dto) {
        return service.createCard(dto);
    }

    @Operation(
            summary = "Обновить статус карты",
            description = "Изменяет статус карты, кроме статуса EXPIRED",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Статус успешно обновлён"),
                    @ApiResponse(responseCode = "400", description = "Некорректный статус"),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена")
            }
    )
    @PatchMapping("/cards/{id}")
    public void updateStatus(
            @Parameter(description = "ID карты") @PathVariable("id") Long id,
            @Parameter(description = "Новый статус карты") @NotNull Card.Status status) {
        if (status.equals(Card.Status.EXPIRED)) {
            throw new CardStatusException(id);
        }
        service.updateStatus(id, status);
    }

    @Operation(
            summary = "Удалить карту",
            description = "Удаляет карту по её ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена")
            }
    )
    @DeleteMapping("/cards/{id}")
    public void deleteCard(
            @Parameter(description = "ID карты") @PathVariable("id") Long id) {
        service.deleteCard(id);
    }
}

