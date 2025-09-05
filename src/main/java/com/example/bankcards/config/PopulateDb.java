package com.example.bankcards.config;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.EncryptionServiceDes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PopulateDb {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardRepository cardRepository;
    private final EncryptionServiceDes encryptionService;

    @PostConstruct
    private void populateUserAndCardTable() {
        var defaultAdmin = User.builder()
                .login("admin")
                .password(passwordEncoder.encode("admin"))
                .role(User.Role.ADMIN)
                .build();
        var defaultUser = User.builder()
                .login("user")
                .password(passwordEncoder.encode("user"))
                .role(User.Role.USER)
                .build();
        var defaultAtm = User.builder()
                .login("atm")
                .password(passwordEncoder.encode("atm"))
                .role(User.Role.ADMIN)
                .build();
        var admin = userRepository.findByLogin(defaultAdmin.getLogin()).orElse(defaultAdmin);
        var user = userRepository.findByLogin(defaultUser.getLogin()).orElse(defaultUser);
        var atm = userRepository.findByLogin(defaultAtm.getLogin()).orElse(defaultAtm);
        userRepository.saveAll(List.of(admin, user, atm));

        var defaultAtmCard = Card.builder()
                .number(encryptionService.encrypt("0000111100001111"))
                .user(atm)
                .validityPeriod(LocalDate.now().plusYears(100))
                .status(Card.Status.ACTIVE)
                .isAtm(true)
                .build();
        var atmCard = cardRepository.findByNumber(defaultAtmCard.getNumber()).orElse(defaultAtmCard);
        cardRepository.save(atmCard);
    }
}
