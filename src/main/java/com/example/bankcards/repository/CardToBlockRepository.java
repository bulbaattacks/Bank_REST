package com.example.bankcards.repository;

import com.example.bankcards.entity.CardToBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardToBlockRepository extends JpaRepository<CardToBlock, Long> {
}
