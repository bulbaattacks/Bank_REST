package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardDto createCard(CardDto dto) {
        var userId = dto.getOwnerId();
        var user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(userId));
        var card = Card.builder()
                .number(dto.getNumber())
                .user(user)
                .validityPeriod(LocalDate.now().plusYears(1))
                .status(dto.getStatus())
                .build();
        cardRepository.save(card);

        dto.setId(card.getId());
        dto.setValidityPeriod(card.getValidityPeriod());
        return dto;
    }

    public List<CardDto> getAllCards() {
        return cardRepository.findAll().stream()
                .map(CardDto::map)
                .toList();
    }

    public List<CardDto> getCardsByUserId(Long userId) {
        return cardRepository.getAllByUserId(userId).stream()
                .map(CardDto::map)
                .toList();
    }

    public void updateStatus(Long id, Card.Status status) {
        var updateResult = cardRepository.updateStatus(id, status);
        if (updateResult == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void deleteCard(Long id) {
        var card = cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        cardRepository.delete(card);
    }
}
