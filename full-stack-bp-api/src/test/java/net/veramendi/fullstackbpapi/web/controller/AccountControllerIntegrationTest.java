package net.veramendi.fullstackbpapi.web.controller;

import net.veramendi.fullstackbpapi.domain.Client;
import net.veramendi.fullstackbpapi.domain.enums.AccountType;
import net.veramendi.fullstackbpapi.domain.enums.Gender;
import net.veramendi.fullstackbpapi.domain.enums.Status;
import net.veramendi.fullstackbpapi.repository.AccountRepository;
import net.veramendi.fullstackbpapi.repository.ClientRepository;
import net.veramendi.fullstackbpapi.repository.MovementRepository;
import net.veramendi.fullstackbpapi.service.ReportPdfGenerator;
import net.veramendi.fullstackbpapi.web.dto.account.AccountCreateRequest;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerIntegrationTest {

    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper objectMapper;
    @Autowired ClientRepository clientRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired MovementRepository movementRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @MockitoBean
    ReportPdfGenerator pdfGenerator;

    MockMvc mockMvc;

    @SuppressWarnings("null")
    @BeforeEach
    void setUp() {
        movementRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Client client = new Client();
        client.setClientId("C-100");
        client.setName("John Smith");
        client.setGender(Gender.MALE);
        client.setAge(42);
        client.setIdentification("ID-99999");
        client.setPassword(passwordEncoder.encode("pwd"));
        client.setStatus(Status.ACTIVE);
        clientRepository.save(client);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("POST /accounts creates a new account linked to an existing client")
    void createAccount_success() throws Exception {
        AccountCreateRequest req = new AccountCreateRequest(
                "ACC-200",
                AccountType.CHECKING,
                new BigDecimal("1000.00"),
                Status.ACTIVE,
                "C-100"
        );

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("ACC-200"))
                .andExpect(jsonPath("$.currentBalance").value(1000.00))
                .andExpect(jsonPath("$.clientId").value("C-100"));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("POST /accounts returns 404 when client does not exist")
    void createAccount_clientNotFound() throws Exception {
        AccountCreateRequest req = new AccountCreateRequest(
                "ACC-404",
                AccountType.SAVINGS,
                new BigDecimal("50.00"),
                null,
                "UNKNOWN"
        );

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client with id 'UNKNOWN' not found"));
    }

    @Test
    @DisplayName("GET /accounts/{id} returns 404 for unknown id")
    void getAccountById_notFound() throws Exception {
        mockMvc.perform(get("/accounts/9999"))
                .andExpect(status().isNotFound());
    }
}
