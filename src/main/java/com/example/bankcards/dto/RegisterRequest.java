package com.example.bankcards.dto;

import com.example.bankcards.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String login;
    private String password;
    private User.Role role;
}
