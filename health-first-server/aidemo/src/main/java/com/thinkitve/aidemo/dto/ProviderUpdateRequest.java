package com.thinkitve.aidemo.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderUpdateRequest {
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid international phone number format")
    private String phoneNumber;

    @Size(min = 3, max = 100, message = "Specialization must be between 3 and 100 characters")
    private String specialization;

    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience cannot exceed 50")
    private Integer yearsOfExperience;

    private ClinicAddressDTO clinicAddress;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClinicAddressDTO {
        @Size(max = 200, message = "Street address cannot exceed 200 characters")
        private String street;

        @Size(max = 100, message = "City name cannot exceed 100 characters")
        private String city;

        @Size(max = 50, message = "State name cannot exceed 50 characters")
        private String state;

        @Pattern(regexp = "^[0-9A-Za-z\\- ]{3,20}$", message = "Invalid ZIP code format")
        private String zip;
    }
} 