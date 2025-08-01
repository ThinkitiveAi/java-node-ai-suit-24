package com.thinkitve.aidemo.service;

import com.thinkitve.aidemo.dto.PatientRegistrationRequest;
import com.thinkitve.aidemo.dto.PatientRegistrationResponse;
import com.thinkitve.aidemo.dto.PatientUpdateRequest;
import com.thinkitve.aidemo.dto.PatientListResponse;
import java.util.UUID;

public interface PatientService {
    PatientRegistrationResponse registerPatient(PatientRegistrationRequest request);
    PatientListResponse getAllPatients(String gender, String city, String state, Boolean isActive, int page, int size);
    PatientRegistrationResponse getPatientById(UUID id);
    PatientRegistrationResponse updatePatient(UUID id, PatientUpdateRequest request);
    PatientRegistrationResponse deletePatient(UUID id);
} 