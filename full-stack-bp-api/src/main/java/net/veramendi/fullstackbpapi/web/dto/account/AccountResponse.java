package net.veramendi.fullstackbpapi.web.dto.account;

import net.veramendi.fullstackbpapi.domain.enums.AccountType;
import net.veramendi.fullstackbpapi.domain.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String accountNumber,
        AccountType accountType,
        BigDecimal initialBalance,
        BigDecimal currentBalance,
        Status status,
        String clientId,
        String clientName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
