package net.veramendi.fullstackbpapi.web.dto.account;

import net.veramendi.fullstackbpapi.domain.enums.AccountType;
import net.veramendi.fullstackbpapi.domain.enums.Status;

public record AccountUpdateRequest(
        String accountNumber,
        AccountType accountType,
        Status status
) {}
