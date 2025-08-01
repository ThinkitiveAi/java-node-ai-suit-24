package com.thinkitve.aidemo.controller;

import com.thinkitve.aidemo.dto.ProviderRegistrationRequest;
import com.thinkitve.aidemo.dto.ProviderRegistrationResponse;
import com.thinkitve.aidemo.dto.ProviderUpdateRequest;
import com.thinkitve.aidemo.dto.ProviderListResponse;
import com.thinkitve.aidemo.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {
    private final ProviderService providerService;

    @PostMapping("/register")
    public ResponseEntity<?> registerProvider(@Valid @RequestBody ProviderRegistrationRequest request) {
        try {
            ProviderRegistrationResponse response = providerService.registerProvider(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ProviderRegistrationResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProviders(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            ProviderListResponse response = providerService.getAllProviders(specialization, city, state, isActive, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ProviderListResponse.builder()
                            .success(false)
                            .message("Error retrieving providers")
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProviderById(@PathVariable String id) {
        try {
            UUID providerId = UUID.fromString(id);
            ProviderRegistrationResponse response = providerService.getProviderById(providerId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProviderRegistrationResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProviderRegistrationResponse.builder()
                            .success(false)
                            .message("Invalid provider ID format")
                            .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvider(
            @PathVariable String id,
            @Valid @RequestBody ProviderUpdateRequest request) {
        try {
            UUID providerId = UUID.fromString(id);
            ProviderRegistrationResponse response = providerService.updateProvider(providerId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ProviderRegistrationResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProviderRegistrationResponse.builder()
                            .success(false)
                            .message("Invalid provider ID format")
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvider(@PathVariable String id) {
        try {
            UUID providerId = UUID.fromString(id);
            ProviderRegistrationResponse response = providerService.deleteProvider(providerId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProviderRegistrationResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProviderRegistrationResponse.builder()
                            .success(false)
                            .message("Invalid provider ID format")
                            .build());
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