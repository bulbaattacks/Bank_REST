package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") Long id, Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        if (authenticatedUser.getRole() != User.Role.ADMIN && !authenticatedUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return userRepository.findById(id).orElseThrow();
    }
}
