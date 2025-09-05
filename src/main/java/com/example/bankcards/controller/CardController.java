package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardStatusException;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Parameter;
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
public class CardController {

    private final CardService service;

    @GetMapping("/cards")
    @PageableAsQueryParam
    public List<CardDto> getAllCards(@PageableDefault(value = 20, sort = "id", direction = Sort.Direction.ASC)
                                     @Parameter(hidden = true) Pageable pageable,
                                     @RequestParam(required = false) Card.Status statusFilter) {
        return service.getAllCards(pageable, statusFilter);
    }

    @GetMapping("/cards/{userId}")
    public List<CardDto> getCardsByUserId(@PathVariable("userId") Long userId, Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        if (authenticatedUser.getRole() != User.Role.ADMIN && !authenticatedUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return service.getCardsByUserId(userId);
    }

    @PostMapping("/cards")
    public CardDto createCard(@Valid @RequestBody CardDto dto) {
        return service.createCard(dto);
    }

    @PatchMapping("/cards/{id}")
    public void updateStatus(@PathVariable("id") Long id, @NotNull Card.Status status) {
        if (status.equals(Card.Status.EXPIRED)) {
            throw new CardStatusException(id);
        }
        service.updateStatus(id, status);
    }

    @DeleteMapping("/cards/{id}")
    public void deleteCard(@PathVariable("id") Long id) {
        service.deleteCard(id);
    }
}

