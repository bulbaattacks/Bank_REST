package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionDto {
    @NotNull
    private Long fromCard;
    @NotNull
    private Long toCard;
    @NotNull
    private Long amount;
}
