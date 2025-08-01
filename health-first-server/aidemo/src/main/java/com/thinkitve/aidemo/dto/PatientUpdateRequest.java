package com.thinkitve.aidemo.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientUpdateRequest {
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    private AddressDTO address;

    private EmergencyContactDTO emergencyContact;

    private InsuranceInfoDTO insuranceInfo;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private List<String> specialRequirements;

    public enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressDTO {
        @Size(max = 200, message = "Street address cannot exceed 200 characters")
        private String street;

        @Size(max = 100, message = "City name cannot exceed 100 characters")
        private String city;

        @Size(max = 50, message = "State name cannot exceed 50 characters")
        private String state;

        @Pattern(regexp = "^[0-9A-Za-z\\- ]{3,20}$", message = "Invalid ZIP code format")
        private String zip;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmergencyContactDTO {
        @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
        private String name;

        @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phone number format")
        private String phone;

        @Size(max = 50, message = "Relationship cannot exceed 50 characters")
        private String relationship;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InsuranceInfoDTO {
        @Size(max = 100, message = "Insurance provider name cannot exceed 100 characters")
        private String provider;

        @Size(max = 50, message = "Policy number cannot exceed 50 characters")
        private String policyNumber;
    }
} 