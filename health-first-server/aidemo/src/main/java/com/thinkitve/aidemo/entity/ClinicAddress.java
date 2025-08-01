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
public class ClinicAddress {
    @Column(name = "street", nullable = false, length = 200)
    @NotBlank
    @Size(max = 200)
    private String street;

    @Column(name = "city", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String state;

    @Column(name = "zip", nullable = false, length = 20)
    @NotBlank
    @Pattern(regexp = "^[0-9A-Za-z -]{3,20}$")
    private String zip;
}
