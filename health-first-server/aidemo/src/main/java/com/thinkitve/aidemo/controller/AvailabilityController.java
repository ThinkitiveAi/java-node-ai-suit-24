package com.thinkitve.aidemo.controller;

import com.thinkitve.aidemo.dto.AvailabilityRequest;
import com.thinkitve.aidemo.dto.AvailabilityResponse;
import com.thinkitve.aidemo.dto.AvailabilitySearchRequest;
import com.thinkitve.aidemo.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    @PostMapping("/provider/availability")
    public ResponseEntity<?> createAvailability(@Valid @RequestBody AvailabilityRequest request) {
        try {
            AvailabilityResponse response = availabilityService.createAvailability(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(AvailabilityResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @GetMapping("/provider/{providerId}/availability")
    public ResponseEntity<?> getProviderAvailability(
            @PathVariable String providerId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            UUID providerUUID = UUID.fromString(providerId);
            AvailabilityResponse response = availabilityService.getAvailability(providerUUID, startDate, endDate, page, size);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AvailabilityResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @PutMapping("/provider/availability/{slotId}")
    public ResponseEntity<?> updateAvailability(
            @PathVariable String slotId,
            @Valid @RequestBody AvailabilityRequest request) {
        try {
            UUID slotUUID = UUID.fromString(slotId);
            AvailabilityResponse response = availabilityService.updateAvailability(slotUUID, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(AvailabilityResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/provider/availability/{slotId}")
    public ResponseEntity<?> deleteAvailability(
            @PathVariable String slotId,
            @RequestParam(defaultValue = "false") boolean deleteRecurring,
            @RequestParam(required = false) String reason) {
        try {
            UUID slotUUID = UUID.fromString(slotId);
            AvailabilityResponse response = availabilityService.deleteAvailability(slotUUID, deleteRecurring, reason);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(AvailabilityResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @GetMapping("/availability/search")
    public ResponseEntity<?> searchAvailableSlots(AvailabilitySearchRequest request) {
        try {
            Page<AvailabilityResponse.AvailabilityData> slots = availabilityService.searchAvailableSlots(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Available slots retrieved successfully");
            response.put("data", slots.getContent());
            response.put("totalElements", slots.getTotalElements());
            response.put("totalPages", slots.getTotalPages());
            response.put("currentPage", slots.getNumber());
            response.put("pageSize", slots.getSize());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error searching available slots"));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Validation failed");
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("errors", errors);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
} 