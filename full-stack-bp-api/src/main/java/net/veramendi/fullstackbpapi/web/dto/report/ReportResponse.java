package net.veramendi.fullstackbpapi.web.dto.report;

import net.veramendi.fullstackbpapi.domain.enums.AccountType;
import net.veramendi.fullstackbpapi.domain.enums.MovementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ReportResponse(
        String clientId,
        String clientName,
        LocalDate from,
        LocalDate to,
        List<AccountStatement> accounts,
        BigDecimal totalCredits,
        BigDecimal totalDebits,
        String pdfBase64
) {

    public record AccountStatement(
            Long accountId,
            String accountNumber,
            AccountType accountType,
            BigDecimal initialBalance,
            BigDecimal currentBalance,
            BigDecimal totalCredits,
            BigDecimal totalDebits,
            List<MovementEntry> movements
    ) {}

    public record MovementEntry(
            LocalDateTime date,
            MovementType movementType,
            BigDecimal value,
            BigDecimal balance
    ) {}
}
