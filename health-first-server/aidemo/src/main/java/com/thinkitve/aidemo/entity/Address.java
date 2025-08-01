package com.thinkitve.aidemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
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
public class Address {
    @Column(name = "street", nullable = false, length = 200)
    @NotBlank(message = "Street address is required")
    @Size(max = 200, message = "Street address cannot exceed 200 characters")
    private String street;

    @Column(name = "city", nullable = false, length = 100)
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name cannot exceed 100 characters")
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State name cannot exceed 50 characters")
    private String state;

    @Column(name = "zip", nullable = false, length = 20)
    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "^[0-9A-Za-z\\- ]{3,20}$", message = "Invalid ZIP code format")
    private String zip;
}
