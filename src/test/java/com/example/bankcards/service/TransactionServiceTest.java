package com.example.bankcards.service;

import com.example.bankcards.TestConfig;
import com.example.bankcards.dto.DepositDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardOwnerException;
import com.example.bankcards.exception.CardStatusNotActiveException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private TransactionCacheService transactionCacheService;

    @InjectMocks
    private TransactionService service;

    @Test
    void createTransaction_throw_ex_card_owner_not_found() {
        Long ownerId = 1L;
        String email = "email";
        Long fromCardId = 1L;
        Long toCardId = 1L;
        TransactionDto dto = new TransactionDto();
        dto.setLogin(email);
        dto.setFromCard(fromCardId);
        dto.setToCard(toCardId);
        Exception exception = assertThrows(CardOwnerException.class, () -> service.createTransaction(dto, ownerId));
        String expected = CardOwnerException.MSG.formatted(fromCardId, ownerId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }

    @Test
    void createTransaction_throw_ex_insufficient_funds() {
        Long ownerId = 1L;
        Long fromCardId = 1L;
        Long toCardId = 1L;
        TransactionDto dto = new TransactionDto();
        dto.setFromCard(fromCardId);
        dto.setToCard(toCardId);
        dto.setAmount(100L);

        Card fromCard = Card.builder()
                .id(fromCardId)
                .status(Card.Status.ACTIVE)
                .build();
        Card toCard = Card.builder()
                .id(toCardId)
                .status(Card.Status.ACTIVE)
                .build();

        when(cardRepository.findByIdAndUserId(any(), any()))
                .thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(any(), any()))
                .thenReturn(Optional.of(toCard));
        when(transactionCacheService.getBalanceFromCache(any()))
                .thenReturn(0L);

        Exception exception = assertThrows(InsufficientFundsException.class, () -> service.createTransaction(dto, ownerId));
        String expected = InsufficientFundsException.MSG.formatted(fromCardId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }

    @Test
    void deposit_throw_ex_card_not_found() {
        Long cardId = 1L;
        DepositDto dto = new DepositDto();
        dto.setToCardId(cardId);
        Exception exception = assertThrows(CardNotFoundException.class, () -> service.deposit(dto));
        String expected = CardNotFoundException.MSG.formatted(cardId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }

    @Test
    void deposit_throw_ex_card_not_active() {
        Long cardId = 1L;
        DepositDto dto = new DepositDto();
        dto.setToCardId(cardId);

        Card card = Card.builder()
                .id(cardId)
                .status(Card.Status.BLOCKED)
                .build();

        when(cardRepository.findByIdAndIsAtmFalse(any()))
                .thenReturn(Optional.of(card));

        Exception exception = assertThrows(CardStatusNotActiveException.class, () -> service.deposit(dto));
        String expected = CardStatusNotActiveException.MSG.formatted(cardId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }
}
