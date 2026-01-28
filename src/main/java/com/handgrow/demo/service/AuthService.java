package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.*;
import com.handgrow.demo.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse registerFarmer(FarmerRegisterRequest request);
    AuthResponse registerCoop(CoopRegisterRequest request);
    AuthResponse registerEnterprise(EnterpriseRegisterRequest request);
}