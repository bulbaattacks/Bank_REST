package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> getAllByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("update Card c set c.status = :status where c.id = :id and c.isAtm is false")
    int updateStatus(@Param(value = "id") Long id, @Param(value = "status") Card.Status status);

    Optional<Card> findByIdAndUserId(Long cardId, Long userId);

    Optional<Card> findByNumber(String number);

    Optional<Card> findFirstByIsAtmTrue();

    @Query("select c from Card c where c.isAtm is false and (:statusFilter is null or c.status = :statusFilter)")
    List<Card> findAllByIsAtmFalseAndStatus(Pageable pageable, Card.Status statusFilter);

    Optional<Card> findByIdAndIsAtmFalse(Long cardId);
}

