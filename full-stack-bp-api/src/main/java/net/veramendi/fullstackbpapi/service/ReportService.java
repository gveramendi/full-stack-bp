package net.veramendi.fullstackbpapi.service;

import net.veramendi.fullstackbpapi.domain.Account;
import net.veramendi.fullstackbpapi.domain.Client;
import net.veramendi.fullstackbpapi.domain.Movement;
import net.veramendi.fullstackbpapi.domain.enums.Status;
import net.veramendi.fullstackbpapi.exception.NotFoundException;
import net.veramendi.fullstackbpapi.repository.AccountRepository;
import net.veramendi.fullstackbpapi.repository.ClientRepository;
import net.veramendi.fullstackbpapi.repository.MovementRepository;
import net.veramendi.fullstackbpapi.web.dto.report.FlatStatementRow;
import net.veramendi.fullstackbpapi.web.dto.report.ReportResponse;
import net.veramendi.fullstackbpapi.web.dto.report.ReportResponse.AccountStatement;
import net.veramendi.fullstackbpapi.web.dto.report.ReportResponse.MovementEntry;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final ReportPdfGenerator pdfGenerator;

    @Transactional(readOnly = true)
    public ReportResponse generate(@NonNull     String clientId, LocalDate from, LocalDate to) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client", clientId));

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay().minusNanos(1);

        List<Account> accounts = accountRepository.findByClient_ClientId(clientId);
        List<Movement> movements = movementRepository.findByClientAndDateRange(clientId, start, end);

        Map<Long, List<Movement>> byAccount = movements.stream()
                .collect(Collectors.groupingBy(m -> m.getAccount().getId()));

        List<AccountStatement> statements = accounts.stream()
                .map(account -> buildStatement(account, byAccount.getOrDefault(account.getId(), List.of())))
                .toList();

        BigDecimal totalCredits = statements.stream()
                .map(AccountStatement::totalCredits)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDebits = statements.stream()
                .map(AccountStatement::totalDebits)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ReportResponse json = new ReportResponse(
                client.getClientId(),
                client.getName(),
                from,
                to,
                statements,
                totalCredits,
                totalDebits,
                null
        );
        
        String pdfBase64 = Base64.getEncoder().encodeToString(pdfGenerator.render(json));
        
        return new ReportResponse(
                json.clientId(),
                json.clientName(),
                json.from(),
                json.to(),
                json.accounts(),
                json.totalCredits(),
                json.totalDebits(),
                pdfBase64
        );
    }

    @Transactional(readOnly = true)
    public List<FlatStatementRow> generateFlat(@NonNull String clientId, LocalDate from, LocalDate to) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client", clientId));

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay().minusNanos(1);

        return movementRepository.findByClientAndDateRange(clientId, start, end).stream()
                .map(m -> {
                    Account account = m.getAccount();
                    return new FlatStatementRow(
                            m.getDate().toLocalDate(),
                            client.getName(),
                            account.getAccountNumber(),
                            account.getAccountType(),
                            account.getInitialBalance(),
                            account.getStatus() == Status.ACTIVE,
                            m.getValue(),
                            m.getBalance()
                    );
                })
                .toList();
    }

    private AccountStatement buildStatement(Account account, List<Movement> movements) {
        BigDecimal credits = movements.stream()
                .filter(m -> m.getValue().signum() > 0)
                .map(Movement::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal debits = movements.stream()
                .filter(m -> m.getValue().signum() < 0)
                .map(m -> m.getValue().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MovementEntry> entries = movements.stream()
                .map(m -> new MovementEntry(m.getDate(), m.getMovementType(), m.getValue(), m.getBalance()))
                .toList();

        return new AccountStatement(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getInitialBalance(),
                account.getCurrentBalance(),
                credits,
                debits,
                entries
        );
    }
}
