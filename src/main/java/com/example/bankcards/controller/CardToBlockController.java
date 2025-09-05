package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardToBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card_to_block")
@RequiredArgsConstructor
public class CardToBlockController {

    private final CardToBlockService cardToBlockService;

    @PostMapping("/add/{cardId}")
    public void addCardToBlock(@PathVariable("cardId") Long cardId, Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        var ownerId = authenticatedUser.getId();
        cardToBlockService.addCardToBlock(cardId, ownerId);
    }

    @GetMapping("/block")
    public void blockCards() {
        cardToBlockService.blockCards();
    }
}
