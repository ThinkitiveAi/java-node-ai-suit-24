package com.thinkitve.aidemo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientLoginRequest {
    @NotBlank(message = "Email or phone number is required")
    private String identifier; // Can be email or phone number

    @NotBlank(message = "Password is required")
    private String password;
} 