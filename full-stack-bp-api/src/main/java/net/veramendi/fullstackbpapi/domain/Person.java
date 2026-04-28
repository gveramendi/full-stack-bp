package net.veramendi.fullstackbpapi.domain;

import net.veramendi.fullstackbpapi.domain.enums.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class Person extends AuditableEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NotNull
    @Min(0)
    @Max(150)
    @Column(nullable = false)
    private Integer age;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String identification;

    @Column
    private String address;

    @Column
    private String phone;
}
