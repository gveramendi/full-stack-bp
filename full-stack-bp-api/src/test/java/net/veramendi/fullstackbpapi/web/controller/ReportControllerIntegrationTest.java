package net.veramendi.fullstackbpapi.web.controller;

import net.veramendi.fullstackbpapi.domain.Account;
import net.veramendi.fullstackbpapi.domain.Client;
import net.veramendi.fullstackbpapi.domain.Movement;
import net.veramendi.fullstackbpapi.domain.enums.AccountType;
import net.veramendi.fullstackbpapi.domain.enums.Gender;
import net.veramendi.fullstackbpapi.domain.enums.MovementType;
import net.veramendi.fullstackbpapi.domain.enums.Status;
import net.veramendi.fullstackbpapi.repository.AccountRepository;
import net.veramendi.fullstackbpapi.repository.ClientRepository;
import net.veramendi.fullstackbpapi.repository.MovementRepository;
import net.veramendi.fullstackbpapi.service.ReportPdfGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ReportControllerIntegrationTest {

    @Autowired WebApplicationContext context;
    @Autowired ClientRepository clientRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired MovementRepository movementRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @MockitoBean
    ReportPdfGenerator pdfGenerator;

    MockMvc mockMvc;
    Client marianela;
    Account checking;

    @BeforeEach
    void setUp() {
        movementRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        marianela = new Client();
        marianela.setClientId("mmontalvo");
        marianela.setName("Marianela Montalvo");
        marianela.setGender(Gender.FEMALE);
        marianela.setAge(32);
        marianela.setIdentification("1700000002");
        marianela.setPassword(passwordEncoder.encode("5678"));
        marianela.setStatus(Status.ACTIVE);
        clientRepository.save(marianela);

        checking = new Account();
        checking.setAccountNumber("225487");
        checking.setAccountType(AccountType.CHECKING);
        checking.setInitialBalance(new BigDecimal("100.00"));
        checking.setCurrentBalance(new BigDecimal("700.00"));
        checking.setStatus(Status.ACTIVE);
        checking.setClient(marianela);
        checking = accountRepository.save(checking);

        Movement deposit = new Movement();
        deposit.setDate(LocalDateTime.of(2022, 2, 10, 10, 0));
        deposit.setMovementType(MovementType.DEPOSIT);
        deposit.setValue(new BigDecimal("600.00"));
        deposit.setBalance(new BigDecimal("700.00"));
        deposit.setAccount(checking);
        movementRepository.save(deposit);
    }

    @Test
    @DisplayName("GET /reports/flat returns one row per movement with English keys")
    void flatStatement_returnsRowPerMovement() throws Exception {
        mockMvc.perform(get("/reports/flat")
                        .param("clientId", "mmontalvo")
                        .param("from", "2022-02-01")
                        .param("to", "2022-02-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date").value("2022-02-10"))
                .andExpect(jsonPath("$[0].client").value("Marianela Montalvo"))
                .andExpect(jsonPath("$[0].accountNumber").value("225487"))
                .andExpect(jsonPath("$[0].accountType").value("CHECKING"))
                .andExpect(jsonPath("$[0].initialBalance").value(100.00))
                .andExpect(jsonPath("$[0].status").value(true))
                .andExpect(jsonPath("$[0].movement").value(600.00))
                .andExpect(jsonPath("$[0].availableBalance").value(700.00));
    }

    @Test
    @DisplayName("GET /reports/flat returns empty list when no movements in range")
    void flatStatement_emptyWhenNoMovementsInRange() throws Exception {
        mockMvc.perform(get("/reports/flat")
                        .param("clientId", "mmontalvo")
                        .param("from", "2030-01-01")
                        .param("to", "2030-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /reports/flat returns 404 when the client does not exist")
    void flatStatement_unknownClient() throws Exception {
        mockMvc.perform(get("/reports/flat")
                        .param("clientId", "ghost")
                        .param("from", "2022-02-01")
                        .param("to", "2022-02-28"))
                .andExpect(status().isNotFound());
    }
}
