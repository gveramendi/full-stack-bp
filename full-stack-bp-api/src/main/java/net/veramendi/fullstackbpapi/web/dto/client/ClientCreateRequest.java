package net.veramendi.fullstackbpapi.web.dto.client;

import net.veramendi.fullstackbpapi.domain.enums.Gender;
import net.veramendi.fullstackbpapi.domain.enums.Status;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientCreateRequest(
        @NotBlank 
        String clientId,

        @NotBlank 
        String name,
        
        @NotNull Gender gender,
        
        @NotNull @Min(0) @Max(150) 
        Integer age,
        
        @NotBlank 
        String identification,
        
        String address,
        String phone,
        
        @NotBlank 
        String password,
        
        Status status
) {}
