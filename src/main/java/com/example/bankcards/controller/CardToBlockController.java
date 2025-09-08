package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardToBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card_to_block")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Card To Block", description = "Операции по добавлению и блокировке карт")
public class CardToBlockController {

    private final CardToBlockService cardToBlockService;

    @Operation(
            summary = "Добавить карту в список на блокировку",
            description = "Добавляет карту в список для последующей блокировки. Доступно только авторизованным пользователям.",
            parameters = {
                    @Parameter(name = "cardId", description = "ID карты, которую нужно добавить в список блокировки", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта успешно добавлена в список на блокировку"),
                    @ApiResponse(responseCode = "401", description = "Неавторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена")
            }
    )
    @PostMapping("/add/{cardId}")
    public void addCardToBlock(@PathVariable("cardId") Long cardId, Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        var ownerId = authenticatedUser.getId();
        cardToBlockService.addCardToBlock(cardId, ownerId);
    }

    @Operation(
            summary = "Заблокировать карты из списка",
            description = "Блокирует все карты, которые находятся в списке на блокировку. Доступно только авторизованным администраторам.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карты успешно заблокированы"),
                    @ApiResponse(responseCode = "401", description = "Неавторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён")
            }
    )
    @GetMapping("/block")
    public void blockCards() {
        cardToBlockService.blockCards();
    }
}
