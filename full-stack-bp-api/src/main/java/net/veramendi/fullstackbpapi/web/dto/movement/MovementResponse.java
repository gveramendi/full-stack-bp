package net.veramendi.fullstackbpapi.web.dto.movement;

import net.veramendi.fullstackbpapi.domain.enums.MovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovementResponse(
        Long id,
        LocalDateTime date,
        MovementType movementType,
        BigDecimal value,
        BigDecimal balance,
        Long accountId,
        String accountNumber
) {}
