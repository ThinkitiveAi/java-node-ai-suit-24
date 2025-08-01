package com.thinkitve.aidemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
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
public class EmergencyContact {
    @Column(name = "name", length = 100)
    @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
    private String name;

    @Column(name = "phone")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phone number format")
    private String phone;

    @Column(name = "relationship", length = 50)
    @Size(max = 50, message = "Relationship cannot exceed 50 characters")
    private String relationship;
}
