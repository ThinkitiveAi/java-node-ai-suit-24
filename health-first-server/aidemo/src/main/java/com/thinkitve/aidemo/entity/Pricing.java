package com.thinkitve.aidemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pricing {
    @Column(name = "base_fee", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Base fee cannot be negative")
    private BigDecimal baseFee;

    @Column(name = "insurance_accepted")
    private boolean insuranceAccepted = false;

    @Column(name = "currency", length = 3)
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency = "USD";
}
