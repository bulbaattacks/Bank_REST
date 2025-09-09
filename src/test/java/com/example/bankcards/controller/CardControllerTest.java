package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.config.JwtAuthFilter;
import com.example.bankcards.security.config.SecurityConfig;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@Import(SecurityConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CardService cardService;

    private User admin = User.builder().id(1L).role(User.Role.ADMIN).build();
    private User user = User.builder().id(1L).role(User.Role.USER).build();

    private Authentication authAdmin = new UsernamePasswordAuthenticationToken(
            admin, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private Authentication authUser = new UsernamePasswordAuthenticationToken(
            user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

    private CardDto card = CardDto.builder()
            .id(1L)
            .number("1111000011110000")
            .ownerId(1L)
            .validityPeriod(LocalDate.now())
            .status(Card.Status.ACTIVE)
            .balance(0L)
            .build();

    @BeforeEach
    void openSecurityFilterChain() throws Exception {
        Mockito.doAnswer(invocation -> {
                    HttpServletRequest req   = invocation.getArgument(0);
                    HttpServletResponse res   = invocation.getArgument(1);
                    FilterChain chain = invocation.getArgument(2);
                    chain.doFilter(req, res);
                    return null;
                }).when(jwtAuthFilter)
                .doFilter(Mockito.any(HttpServletRequest.class),
                        Mockito.any(HttpServletResponse.class),
                        Mockito.any(FilterChain.class));
    }

    @Test
    void getAllCards_asAdmin_shouldReturnOk() throws Exception {
        Mockito.when(cardService.getAllCards(Mockito.any(Pageable.class), Mockito.any()))
                .thenReturn(List.of(card));

        mockMvc.perform(get("/cards")
                        .with(authentication(authAdmin))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(card.getId()));
    }

    @Test
    void getAllCardsForbidden_asUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/cards")
                        .with(authentication(authUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCardsByUserId_asAdmin_shouldReturnOk() throws Exception {
        Mockito.when(cardService.getCardsByUserId(admin.getId())).thenReturn(List.of(card));

        mockMvc.perform(get("/cards/1")
                        .with(authentication(authAdmin))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(card.getId()));
    }

    @Test
    void getCardsByUserId_asOwner_shouldReturnOk() throws Exception {
        Mockito.when(cardService.getCardsByUserId(user.getId())).thenReturn(List.of(card));

        mockMvc.perform(get("/cards/1")
                        .with(authentication(authUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(card.getId()));
    }

    @Test
    void getCardsByUserId_asNotOwnerUser_shouldReturnForbidden() throws Exception {
        Mockito.when(cardService.getCardsByUserId(user.getId())).thenReturn(List.of(card));

        mockMvc.perform(get("/cards/2").with(authentication(authUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCard_AsAdmin_shouldReturnOk() throws Exception {
        Mockito.when(cardService.createCard(Mockito.any(CardDto.class))).thenReturn(card);
        var body = objectMapper.writeValueAsString(card);

        mockMvc.perform(post("/cards")
                        .with(authentication(authAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(card.getId()))
                .andExpect(jsonPath("$.number").value(card.getNumber()))
                .andExpect(jsonPath("$.ownerId").value(card.getOwnerId()));
    }

    @Test
    void createCard_AsUser_shouldReturnForbidden() throws Exception {
        Mockito.when(cardService.createCard(Mockito.any(CardDto.class))).thenReturn(card);
        var body = objectMapper.writeValueAsString(card);

        mockMvc.perform(post("/cards")
                        .with(authentication(authUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateStatus_AsAdmin_shouldReturnOk() throws Exception {
        Mockito.doNothing()
                .when(cardService)
                .updateStatus(card.getId(), Card.Status.BLOCKED);

        mockMvc.perform(patch("/cards/1")
                        .with(authentication(authAdmin))
                        .param("status", Card.Status.BLOCKED.name()))
                .andExpect(status().isOk());

        Mockito.verify(cardService)
                .updateStatus(card.getId(), Card.Status.BLOCKED);
    }

    @Test
    void updateStatus_AsUser_shouldReturnForbidden() throws Exception {
        Mockito.doNothing()
                .when(cardService)
                .updateStatus(card.getId(), Card.Status.BLOCKED);

        mockMvc.perform(patch("/cards/1")
                        .with(authentication(authUser))
                        .param("status", Card.Status.BLOCKED.name()))
                .andExpect(status().isForbidden());

        Mockito.verify(cardService, Mockito.times(0))
                .updateStatus(card.getId(), Card.Status.BLOCKED);
    }

    @Test
    void updateStatusExpired_AsAdmin_shouldReturnBadRequest() throws Exception {
        Mockito.doNothing()
                .when(cardService)
                .updateStatus(card.getId(), Card.Status.EXPIRED);

        mockMvc.perform(patch("/cards/1")
                        .with(authentication(authAdmin))
                        .param("status", Card.Status.EXPIRED.name()))
                .andExpect(status().isBadRequest());

        Mockito.verify(cardService, Mockito.times(0))
                .updateStatus(card.getId(), Card.Status.BLOCKED);
    }


    @Test
    void deleteCard_AsAdmin_shouldReturnOk() throws Exception {
        Mockito.doNothing()
                .when(cardService)
                .deleteCard(card.getId());

        mockMvc.perform(delete("/cards/1")
                        .with(authentication(authAdmin)))
                .andExpect(status().isOk());

        Mockito.verify(cardService)
                .deleteCard(card.getId());
    }

    @Test
    void deleteCard_AsUser_shouldReturnForbidden() throws Exception {
        Mockito.doNothing()
                .when(cardService)
                .deleteCard(card.getId());

        mockMvc.perform(delete("/cards/1")
                        .with(authentication(authUser)))
                .andExpect(status().isForbidden());

        Mockito.verify(cardService, Mockito.times(0))
                .deleteCard(card.getId());
    }
}