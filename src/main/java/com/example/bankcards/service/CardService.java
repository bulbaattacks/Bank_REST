package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EncryptionServiceDes encryptionService;
    private final TransactionService transactionService;

    public CardDto createCard(CardDto dto) {
        var userId = dto.getOwnerId();
        var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        var card = Card.builder()
                .number(encryptionService.encrypt(dto.getNumber()))
                .user(user)
                .validityPeriod(LocalDate.now().plusYears(1))
                .status(dto.getStatus())
                .build();
        cardRepository.save(card);

        dto.setId(card.getId());
        dto.setValidityPeriod(card.getValidityPeriod());
        dto.setNumber(encryptionService.decrypt(card.getNumber()));
        dto.setBalance(transactionService.getBalanceFromCache(card.getId()));
        dto.hideNumber();
        return dto;
    }

    public List<CardDto> getAllCards(Pageable pageable, Card.Status statusFilter) {
        return cardRepository.findAllByIsAtmFalseAndStatus(pageable, statusFilter).stream()
                .map(entity -> {
                    var card = CardDto.builder()
                            .id(entity.getId())
                            .number(encryptionService.decrypt(entity.getNumber()))
                            .ownerId(entity.getUser().getId())
                            .validityPeriod(entity.getValidityPeriod())
                            .status(entity.getStatus())
                            .isAtm(entity.isAtm())
                            .balance(transactionService.getBalanceFromCache(entity.getId()))
                            .build();
                    card.hideNumber();
                    return card;
                })
                .toList();
    }

    public List<CardDto> getCardsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return cardRepository.getAllByUserId(userId).stream()
                .map(entity -> {
                    var card = CardDto.builder()
                            .id(entity.getId())
                            .number(encryptionService.decrypt(entity.getNumber()))
                            .ownerId(entity.getUser().getId())
                            .validityPeriod(entity.getValidityPeriod())
                            .status(entity.getStatus())
                            .isAtm(entity.isAtm())
                            .balance(transactionService.getBalanceFromCache(entity.getId()))
                            .build();
                    card.hideNumber();
                    return card;
                })
                .toList();
    }

    public void updateStatus(Long id, Card.Status status) {
        var updateResult = cardRepository.updateStatus(id, status);
        if (updateResult == 0) {
            throw new CardNotFoundException(id);
        }
    }

    public void deleteCard(Long id) {
        var card = cardRepository.findByIdAndIsAtmFalse(id).orElseThrow(() -> new CardNotFoundException(id));
        cardRepository.delete(card);
    }
}
