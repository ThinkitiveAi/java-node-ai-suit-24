package com.thinkitve.aidemo.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilitySearchRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String specialization;
    private String location;
    private String appointmentType;
    private boolean insuranceAccepted;
    private BigDecimal maxPrice;
    private String timezone;
    private List<String> providerIds;
    private String status = "AVAILABLE";
    private int page = 0;
    private int size = 20;
} 