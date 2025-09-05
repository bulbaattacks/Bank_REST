package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TransactionDto {
    @NotNull
    private Long fromCard;
    @NotNull
    private Long toCard;
    @NotNull
    private Long amount;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String login;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date;
}
