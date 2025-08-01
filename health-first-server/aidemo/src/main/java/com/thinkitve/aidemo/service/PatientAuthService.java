package com.thinkitve.aidemo.service;

import com.thinkitve.aidemo.dto.PatientLoginRequest;
import com.thinkitve.aidemo.dto.PatientLoginResponse;

public interface PatientAuthService {
    PatientLoginResponse login(PatientLoginRequest request);
} 