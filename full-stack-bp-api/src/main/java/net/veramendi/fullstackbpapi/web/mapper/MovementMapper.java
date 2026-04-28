package net.veramendi.fullstackbpapi.web.mapper;

import net.veramendi.fullstackbpapi.domain.Movement;
import net.veramendi.fullstackbpapi.web.dto.movement.MovementResponse;

public final class MovementMapper {

    private MovementMapper() {}

    public static MovementResponse toResponse(Movement m) {
        return new MovementResponse(
                m.getId(),
                m.getDate(),
                m.getMovementType(),
                m.getValue(),
                m.getBalance(),
                m.getAccount().getId(),
                m.getAccount().getAccountNumber()
        );
    }
}
