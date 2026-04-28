package net.veramendi.fullstackbpapi.web.dto.account;

import net.veramendi.fullstackbpapi.domain.enums.AccountType;
import net.veramendi.fullstackbpapi.domain.enums.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AccountCreateRequest(
        
        @NotBlank 
        String accountNumber,
        
        @NotNull 
        AccountType accountType,
        
        @NotNull @PositiveOrZero 
        BigDecimal initialBalance,
        
        Status status,

        @NotBlank String clientId
) {}
