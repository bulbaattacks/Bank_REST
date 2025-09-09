package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.config.JwtAuthFilter;
import com.example.bankcards.security.config.SecurityConfig;
import com.example.bankcards.service.CardToBlockService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardToBlockController.class)
@Import(SecurityConfig.class)
public class CardToBlockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CardToBlockService cardToBlockService;

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
    void addCardToBlock_AsAdmin_shouldReturnOk() throws Exception {
        Mockito.doNothing().when(cardToBlockService).addCardToBlock(card.getId(), admin.getId());

        mockMvc.perform(post("/card_to_block/add/1")
                .with(authentication(authAdmin)))
                .andExpect(status().isOk());

        Mockito.verify(cardToBlockService).addCardToBlock(card.getId(), admin.getId());
    }

    @Test
    void addCardToBlock_AsOwner_shouldReturnOk() throws Exception {
        Mockito.doNothing().when(cardToBlockService).addCardToBlock(card.getId(), user.getId());

        mockMvc.perform(post("/card_to_block/add/1")
                        .with(authentication(authUser)))
                .andExpect(status().isOk());

        Mockito.verify(cardToBlockService).addCardToBlock(card.getId(), user.getId());
    }

    @Test
    void blockCard_AsAdmin_shouldReturnOk() throws Exception {
        Mockito.doNothing().when(cardToBlockService).blockCards();

        mockMvc.perform(get("/card_to_block/block")
                        .with(authentication(authAdmin)))
                .andExpect(status().isOk());

        Mockito.verify(cardToBlockService).blockCards();
    }

    @Test
    void blockCard_AsUser_shouldReturnForbidden() throws Exception {
        Mockito.doNothing().when(cardToBlockService).blockCards();

        mockMvc.perform(get("/card_to_block/block")
                        .with(authentication(authUser)))
                .andExpect(status().isForbidden());

        Mockito.verify(cardToBlockService, Mockito.times(0)).blockCards();
    }

}
