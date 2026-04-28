package net.veramendi.fullstackbpapi.web.controller;

import net.veramendi.fullstackbpapi.domain.Account;
import net.veramendi.fullstackbpapi.domain.Client;
import net.veramendi.fullstackbpapi.domain.enums.AccountType;
import net.veramendi.fullstackbpapi.domain.enums.Gender;
import net.veramendi.fullstackbpapi.domain.enums.MovementType;
import net.veramendi.fullstackbpapi.domain.enums.Status;
import net.veramendi.fullstackbpapi.repository.AccountRepository;
import net.veramendi.fullstackbpapi.repository.ClientRepository;
import net.veramendi.fullstackbpapi.repository.MovementRepository;
import net.veramendi.fullstackbpapi.service.ReportPdfGenerator;
import net.veramendi.fullstackbpapi.web.dto.movement.MovementRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class MovementControllerIntegrationTest {

    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper objectMapper;
    @Autowired ClientRepository clientRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired MovementRepository movementRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @MockitoBean
    ReportPdfGenerator pdfGenerator;

    MockMvc mockMvc;
    Account account;

    @SuppressWarnings("null")
    @BeforeEach
    void setUp() {
        movementRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Client client = new Client();
        client.setClientId("C-001");
        client.setName("Jane Doe");
        client.setGender(Gender.FEMALE);
        client.setAge(30);
        client.setIdentification("ID-12345");
        client.setPassword(passwordEncoder.encode("secret"));
        client.setStatus(Status.ACTIVE);
        clientRepository.save(client);

        account = new Account();
        account.setAccountNumber("ACC-001");
        account.setAccountType(AccountType.SAVINGS);
        account.setInitialBalance(new BigDecimal("500.00"));
        account.setCurrentBalance(new BigDecimal("500.00"));
        account.setStatus(Status.ACTIVE);
        account.setClient(client);
        account = accountRepository.save(account);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("POST /movements creates a deposit and updates account balance")
    void createDeposit_success() throws Exception {
        MovementRequest req = new MovementRequest(account.getId(), MovementType.DEPOSIT, new BigDecimal("200.00"));

        mockMvc.perform(post("/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movementType").value("DEPOSIT"))
                .andExpect(jsonPath("$.value").value(200.00))
                .andExpect(jsonPath("$.balance").value(700.00));

        assertThat(accountRepository.findById(account.getId()).orElseThrow().getCurrentBalance())
                .isEqualByComparingTo("700.00");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("POST /movements returns 422 with 'Insufficient balance' when withdrawal exceeds current balance")
    void createWithdrawal_insufficientBalance() throws Exception {
        MovementRequest req = new MovementRequest(account.getId(), MovementType.WITHDRAWAL, new BigDecimal("600.00"));

        mockMvc.perform(post("/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Insufficient balance"));

        assertThat(movementRepository.count()).isZero();
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("POST /movements returns 422 with 'Daily withdrawal limit exceeded' when over $1000/day per account")
    void createWithdrawal_dailyLimitExceeded() throws Exception {
        account.setCurrentBalance(new BigDecimal("5000.00"));
        accountRepository.save(account);

        MovementRequest first = new MovementRequest(account.getId(), MovementType.WITHDRAWAL, new BigDecimal("800.00"));
        mockMvc.perform(post("/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        MovementRequest exceeding = new MovementRequest(account.getId(), MovementType.WITHDRAWAL, new BigDecimal("300.00"));
        mockMvc.perform(post("/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exceeding)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Daily withdrawal limit exceeded"));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("POST /movements creates a withdrawal within limit and stores a negative value")
    void createWithdrawal_success() throws Exception {
        MovementRequest req = new MovementRequest(account.getId(), MovementType.WITHDRAWAL, new BigDecimal("150.00"));

        mockMvc.perform(post("/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movementType").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.value").value(-150.00))
                .andExpect(jsonPath("$.balance").value(350.00));
    }
}
