package net.veramendi.fullstackbpapi.web.dto.report;

import net.veramendi.fullstackbpapi.domain.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FlatStatementRow(
        LocalDate date,
        String client,
        String accountNumber,
        AccountType accountType,
        BigDecimal initialBalance,
        boolean status,
        BigDecimal movement,
        BigDecimal availableBalance
) {}
