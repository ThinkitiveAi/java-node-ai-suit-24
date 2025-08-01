package com.thinkitve.aidemo.service;

import com.thinkitve.aidemo.dto.LoginRequest;
import com.thinkitve.aidemo.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
} 