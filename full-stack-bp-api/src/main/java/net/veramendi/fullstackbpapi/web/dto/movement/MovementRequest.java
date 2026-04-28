package net.veramendi.fullstackbpapi.web.dto.movement;

import net.veramendi.fullstackbpapi.domain.enums.MovementType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MovementRequest(
        
        @NotNull 
        Long accountId,
        
        @NotNull 
        MovementType movementType,
        
        @NotNull @Positive BigDecimal amount
) {}
