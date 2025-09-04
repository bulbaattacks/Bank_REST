package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotNull
    private Integer number;
    @NotNull
    private Long ownerId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate validityPeriod;
    @NotNull
    private Card.Status status;

    public static CardDto map(Card entity) {
        return CardDto.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .ownerId(entity.getUser().getId())
                .status(entity.getStatus())
                .build();
    }
}