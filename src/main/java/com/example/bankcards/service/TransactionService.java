package com.example.bankcards.service;

import com.example.bankcards.dto.DepositDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CardRepository cardRepository;
    private final TransactionCacheService transactionCacheService;

    public void createTransaction(TransactionDto dto, Long ownerId) {
        var fromCard = cardRepository.findByIdAndUserId(dto.getFromCard(), ownerId)
                .orElseThrow(() -> new CardOwnerException(dto.getFromCard(), ownerId));
        var toCard = cardRepository.findByIdAndUserId(dto.getToCard(), ownerId)
                .orElseThrow(() -> new CardOwnerException(dto.getToCard(), ownerId));

        cardIsActive(fromCard);
        cardIsActive(toCard);

        var balanceFromCard = getBalanceFromCache(fromCard.getId());
        if (balanceFromCard - dto.getAmount() < 0) {
            throw new InsufficientFundsException(fromCard.getId());
        }

        var transaction = Transaction.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(dto.getAmount())
                .user(fromCard.getUser())
                .date(LocalDate.now())
                .build();
        transactionCacheService.save(transaction, fromCard.getId(), toCard.getId());
    }

    public void deposit(DepositDto dto) {
        var toCard = cardRepository.findByIdAndIsAtmFalse(dto.getToCardId())
                .orElseThrow(() -> new CardNotFoundException(dto.getToCardId()));
        cardIsActive(toCard);
        var atm = cardRepository.findFirstByIsAtmTrue()
                .orElseThrow(ATMNotFoundException::new);

        var transaction = Transaction.builder()
                .fromCard(atm)
                .toCard(toCard)
                .amount(dto.getAmount())
                .user(toCard.getUser())
                .date(LocalDate.now())
                .build();
        transactionCacheService.save(transaction, toCard.getId());
    }

    public Long getBalanceFromCache(Long cardId) {
        return transactionCacheService.getBalanceFromCache(cardId);
    }

    public List<TransactionDto> getAllTransactionHistory(Pageable pageable, Long amountFilter) {
        return transactionCacheService.findAll(pageable, amountFilter).stream()
                .map(entity -> {
                    var dto = new TransactionDto();
                    dto.setFromCard(entity.getFromCard().getId());
                    dto.setToCard(entity.getToCard().getId());
                    dto.setAmount(entity.getAmount());
                    dto.setLogin(entity.getUser().getLogin());
                    dto.setDate(entity.getDate());
                    return dto;
                })
                .toList();
    }

    public List<TransactionDto> getTransactionHistoryByUserId(Long userId, Pageable pageable, Long amountFilter) {
        return transactionCacheService.findAllByUserId(userId, pageable, amountFilter).stream()
                .map(entity -> {
                    var dto = new TransactionDto();
                    dto.setFromCard(entity.getFromCard().getId());
                    dto.setToCard(entity.getToCard().getId());
                    dto.setAmount(entity.getAmount());
                    dto.setLogin(entity.getUser().getLogin());
                    dto.setDate(entity.getDate());
                    return dto;
                })
                .toList();
    }

    private void cardIsActive(Card card) {
        if (card.getStatus() != Card.Status.ACTIVE) {
            throw new CardStatusNotActiveException(card.getId());
        }
    }
}
