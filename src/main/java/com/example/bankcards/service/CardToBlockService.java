package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardToBlock;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardToBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardToBlockService {

    private final CardToBlockRepository cardToBlockRepository;
    private final CardRepository cardRepository;

    public void addCardToBlock(Long cardId, Long ownerId) {
        var card = cardRepository.findByIdAndUserId(cardId, ownerId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        var cardToBlock = new CardToBlock();
        cardToBlock.setCard(card);
        cardToBlockRepository.save(cardToBlock);
    }

    public void blockCards() {
        List<Card> cardsToBlock = cardToBlockRepository.findAll().stream().map(CardToBlock::getCard).toList();
        cardsToBlock.forEach(card -> card.setStatus(Card.Status.BLOCKED));
        cardRepository.saveAll(cardsToBlock);
        cardToBlockRepository.deleteAll();
    }
}
