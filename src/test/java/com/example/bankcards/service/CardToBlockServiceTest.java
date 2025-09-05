package com.example.bankcards.service;

import com.example.bankcards.TestConfig;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardToBlockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
class CardToBlockServiceTest {

    @Mock
    private CardToBlockRepository cardToBlockRepository;
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardToBlockService service;

    @Test
    void addCardToBlock() {
        Long cardId = 1L;
        Long ownerId = 1L;
        Exception exception = assertThrows(CardNotFoundException.class, () -> service.addCardToBlock(cardId, ownerId));
        String expected = CardNotFoundException.MSG.formatted(cardId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }
}
