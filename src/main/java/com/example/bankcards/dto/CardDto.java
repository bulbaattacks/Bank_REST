package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank
    @Size(min = 16, max = 16)
    @Pattern(regexp = "^[0-9]*$")
    private String number;
    @NotNull
    private Long ownerId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate validityPeriod;
    @NotNull
    private Card.Status status;

    public void hideNumber() {
        var subNumber = number.substring(12);
        number = "**** **** **** " + subNumber;
    }
}