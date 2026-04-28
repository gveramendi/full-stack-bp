package net.veramendi.fullstackbpapi.web.dto.client;

import net.veramendi.fullstackbpapi.domain.enums.Gender;
import net.veramendi.fullstackbpapi.domain.enums.Status;

import java.time.LocalDateTime;

public record ClientResponse(
        String clientId,
        String name,
        Gender gender,
        Integer age,
        String identification,
        String address,
        String phone,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
