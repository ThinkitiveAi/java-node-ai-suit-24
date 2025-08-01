package com.thinkitve.aidemo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderRegistrationRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid international phone number format")
    private String phoneNumber;

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 3, max = 100)
    private String specialization;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "License number must be alphanumeric")
    private String licenseNumber;

    @Min(0)
    @Max(50)
    private Integer yearsOfExperience;

    @NotNull
    private ClinicAddressDTO clinicAddress;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClinicAddressDTO {
        @NotBlank
        @Size(max = 200)
        private String street;

        @NotBlank
        @Size(max = 100)
        private String city;

        @NotBlank
        @Size(max = 50)
        private String state;

        @NotBlank
        @Pattern(
                regexp = "^[0-9A-Za-z\\- ]{3,20}$",
                message = "Invalid zip code"
        )
        private String zip;

    }
} 