package com.example.bankcards.controller;

import com.example.bankcards.dto.DepositDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.config.JwtAuthFilter;
import com.example.bankcards.security.config.SecurityConfig;
import com.example.bankcards.service.TransactionService;
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

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private TransactionService transactionService;

    private User admin = User.builder().id(1L).role(User.Role.ADMIN).build();
    private User user = User.builder().id(1L).role(User.Role.USER).build();

    private Authentication authAdmin = new UsernamePasswordAuthenticationToken(
            admin, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private Authentication authUser = new UsernamePasswordAuthenticationToken(
            user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

    @BeforeEach
    void openSecurityFilterChain() throws Exception {
        Mockito.doAnswer(invocation -> {
                    HttpServletRequest req = invocation.getArgument(0);
                    HttpServletResponse res = invocation.getArgument(1);
                    FilterChain chain = invocation.getArgument(2);
                    chain.doFilter(req, res);
                    return null;
                }).when(jwtAuthFilter)
                .doFilter(Mockito.any(HttpServletRequest.class),
                        Mockito.any(HttpServletResponse.class),
                        Mockito.any(FilterChain.class));
    }

    @Test
    void createTransaction_AsAdmin_shouldReturnOk() throws Exception {
        var transactionDto = new TransactionDto();
        transactionDto.setFromCard(1L);
        transactionDto.setToCard(2L);
        transactionDto.setAmount(1000L);

        Mockito.doNothing()
                .when(transactionService)
                .createTransaction(Mockito.any(TransactionDto.class), Mockito.eq(1L));

        var body = objectMapper.writeValueAsString(transactionDto);

        mockMvc.perform(post("/transaction")
                        .with(authentication(authAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(transactionService)
                .createTransaction(Mockito.any(TransactionDto.class), Mockito.eq(1L));
    }

    @Test
    void createTransaction_AsOwner_shouldReturnOk() throws Exception {
        var transactionDto = new TransactionDto();
        transactionDto.setFromCard(1L);
        transactionDto.setToCard(2L);
        transactionDto.setAmount(1000L);

        Mockito.doNothing()
                .when(transactionService)
                .createTransaction(Mockito.any(TransactionDto.class), Mockito.eq(1L));

        var body = objectMapper.writeValueAsString(transactionDto);

        mockMvc.perform(post("/transaction")
                        .with(authentication(authUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(transactionService)
                .createTransaction(Mockito.any(TransactionDto.class), Mockito.eq(1L));
    }

    @Test
    void deposit_AsAdmin_shouldReturnOk() throws Exception {
        var depositDto = new DepositDto();
        depositDto.setToCardId(1L);
        depositDto.setAmount(100L);

        Mockito.doNothing()
                .when(transactionService)
                .deposit(Mockito.any(DepositDto.class));

        var body = objectMapper.writeValueAsString(depositDto);

        mockMvc.perform(post("/deposit")
                        .with(authentication(authAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(transactionService)
                .deposit(Mockito.any(DepositDto.class));
    }

    @Test
    void deposit_AsUser_shouldReturnOk() throws Exception {
        var depositDto = new DepositDto();
        depositDto.setToCardId(1L);
        depositDto.setAmount(100L);

        Mockito.doNothing()
                .when(transactionService)
                .deposit(Mockito.any(DepositDto.class));

        var body = objectMapper.writeValueAsString(depositDto);

        mockMvc.perform(post("/deposit")
                        .with(authentication(authUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(transactionService)
                .deposit(Mockito.any(DepositDto.class));
    }

    @Test
    void getTransactionHistory_AsAdmin_shouldReturnOk() throws Exception {
        var transactionDto = new TransactionDto();
        transactionDto.setFromCard(1L);
        transactionDto.setToCard(2L);
        transactionDto.setAmount(1000L);

        Mockito
                .when(transactionService.getAllTransactionHistory(Mockito.any(Pageable.class), Mockito.eq(1L)))
                .thenReturn(List.of(transactionDto));
        Mockito
                .when(transactionService.getTransactionHistoryByUserId(Mockito.any(), Mockito.any(Pageable.class), Mockito.any()))
                .thenReturn(List.of(transactionDto));

        var body = objectMapper.writeValueAsString(transactionDto);

        mockMvc.perform(get("/transaction_history")
                        .with(authentication(authAdmin))
                        .param("amountFilter", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(transactionService)
                .getAllTransactionHistory(Mockito.any(Pageable.class), Mockito.eq(1L));
        Mockito.verify(transactionService, Mockito.times(0))
                .getTransactionHistoryByUserId(Mockito.any(), Mockito.any(Pageable.class), Mockito.any());
    }

    @Test
    void getTransactionHistoryByUserId_AsUser_shouldReturnOk() throws Exception {
        var transactionDto = new TransactionDto();
        transactionDto.setFromCard(1L);
        transactionDto.setToCard(2L);
        transactionDto.setAmount(1000L);

        Mockito
                .when(transactionService.getAllTransactionHistory(Mockito.any(Pageable.class), Mockito.any()))
                .thenReturn(List.of(transactionDto));
        Mockito
                .when(transactionService.getTransactionHistoryByUserId(Mockito.eq(1L), Mockito.any(Pageable.class), Mockito.eq(1L)))
                .thenReturn(List.of(transactionDto));

        var body = objectMapper.writeValueAsString(transactionDto);

        mockMvc.perform(get("/transaction_history")
                        .with(authentication(authUser))
                        .param("amountFilter", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(transactionService, Mockito.times(0))
                .getAllTransactionHistory(Mockito.any(Pageable.class), Mockito.any());
        Mockito.verify(transactionService)
                .getTransactionHistoryByUserId(Mockito.eq(1L), Mockito.any(Pageable.class), Mockito.eq(1L));
    }
}
