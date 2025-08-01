package com.thinkitve.aidemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
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
public class Location {
    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    @NotNull(message = "Location type is required")
    private ProviderAvailability.LocationType type;

    @Column(name = "address", length = 500)
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @Column(name = "room_number", length = 50)
    @Size(max = 50, message = "Room number cannot exceed 50 characters")
    private String roomNumber;
}
