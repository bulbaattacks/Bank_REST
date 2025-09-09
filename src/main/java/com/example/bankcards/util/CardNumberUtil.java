package com.example.bankcards.util;

import com.example.bankcards.dto.CardDto;

public interface CardNumberUtil {

    static void hide(CardDto card){
        var subNumber = card.getNumber().substring(12);
        card.setNumber("**** **** **** " + subNumber);
    }
}
