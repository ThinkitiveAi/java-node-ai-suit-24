package com.thinkitve.aidemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceInfo {
    @Column(name = "provider", length = 100)
    @Size(max = 100, message = "Insurance provider name cannot exceed 100 characters")
    private String provider;

    @Column(name = "policy_number", length = 50)
    @Size(max = 50, message = "Policy number cannot exceed 50 characters")
    private String policyNumber;
}
