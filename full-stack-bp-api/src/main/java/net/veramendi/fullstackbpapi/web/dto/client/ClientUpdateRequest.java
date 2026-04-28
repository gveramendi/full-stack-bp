package net.veramendi.fullstackbpapi.web.dto.client;

import net.veramendi.fullstackbpapi.domain.enums.Gender;
import net.veramendi.fullstackbpapi.domain.enums.Status;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ClientUpdateRequest(
        String name,

        Gender gender,
        
        @Min(0) @Max(150) Integer age,
        
        String identification,
        
        String address,
        
        String phone,
        
        String password,
        
        Status status
) {}
