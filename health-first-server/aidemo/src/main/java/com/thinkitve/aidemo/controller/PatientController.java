package com.thinkitve.aidemo.controller;

import com.thinkitve.aidemo.dto.PatientRegistrationRequest;
import com.thinkitve.aidemo.dto.PatientRegistrationResponse;
import com.thinkitve.aidemo.dto.PatientUpdateRequest;
import com.thinkitve.aidemo.dto.PatientListResponse;
import com.thinkitve.aidemo.service.PatientService;
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
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        try {
            PatientRegistrationResponse response = patientService.registerPatient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(PatientRegistrationResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPatients(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            PatientListResponse response = patientService.getAllPatients(gender, city, state, isActive, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PatientListResponse.builder()
                            .success(false)
                            .message("Error retrieving patients")
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable String id) {
        try {
            UUID patientId = UUID.fromString(id);
            PatientRegistrationResponse response = patientService.getPatientById(patientId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(PatientRegistrationResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PatientRegistrationResponse.builder()
                            .success(false)
                            .message("Invalid patient ID format")
                            .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable String id,
            @Valid @RequestBody PatientUpdateRequest request) {
        try {
            UUID patientId = UUID.fromString(id);
            PatientRegistrationResponse response = patientService.updatePatient(patientId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(PatientRegistrationResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PatientRegistrationResponse.builder()
                            .success(false)
                            .message("Invalid patient ID format")
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable String id) {
        try {
            UUID patientId = UUID.fromString(id);
            PatientRegistrationResponse response = patientService.deletePatient(patientId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(PatientRegistrationResponse.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PatientRegistrationResponse.builder()
                            .success(false)
                            .message("Invalid patient ID format")
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