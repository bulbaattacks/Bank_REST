package com.example.bankcards.service;

import com.example.bankcards.dto.DepositDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    private final ConcurrentHashMap<Long, Long> cardBalanceCashe = new ConcurrentHashMap<>();

    public void createTransaction(TransactionDto dto, Long ownerId) {
        var fromCard = cardRepository.findByIdAndUserId(dto.getFromCard(), ownerId).orElseThrow();
        var toCard = cardRepository.findByIdAndUserId(dto.getToCard(), ownerId).orElseThrow();

        var balanceFromCard = getBalanceFromCache(fromCard.getId());
        if (balanceFromCard - dto.getAmount() < 0) {
            throw new RuntimeException("Not enough money on card");
        }

        var transaction = Transaction.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(dto.getAmount())
                .build();
        transactionRepository.save(transaction);

        findInDbAndUpdateCache(fromCard.getId());
        findInDbAndUpdateCache(toCard.getId());
    }

    public void deposit(DepositDto dto) {
        var toCard = cardRepository.findByIdAndIsAtmFalse(dto.getToCardId()).orElseThrow();
        var atm = cardRepository.findFirstByIsAtmTrue().orElseThrow();

        var transaction = Transaction.builder()
                .fromCard(atm)
                .toCard(toCard)
                .amount(dto.getAmount())
                .build();
        transactionRepository.save(transaction);
        findInDbAndUpdateCache(toCard.getId());
    }

    public Long getBalanceFromCache(Long cardId) {

        var balance = cardBalanceCashe.get(cardId);
        return balance != null ? balance : findInDbAndUpdateCache(cardId);
    }

    private Long findInDbAndUpdateCache(Long cardId) {
        var balance = getBalance(cardId);
        cardBalanceCashe.put(cardId, balance);
        return balance;
    }

    private Long getBalance(Long cardId) {
        var depositAmount = transactionRepository.countDepositAmount(cardId);
        var withdrawAmount = transactionRepository.countWithdrawAmount(cardId);
        return depositAmount - withdrawAmount;
    }
}
