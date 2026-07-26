package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.*;
import com.handgrow.demo.dto.response.AuthResponse;
import com.handgrow.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register/farmer")
    public ResponseEntity<AuthResponse> registerFarmer(@Valid @RequestBody FarmerRegisterRequest request) {
        return ResponseEntity.ok(authService.registerFarmer(request));
    }

    @PostMapping("/register/coop")
    public ResponseEntity<AuthResponse> registerCoop(@Valid @RequestBody CoopRegisterRequest request) {
        return ResponseEntity.ok(authService.registerCoop(request));
    }

    @PostMapping("/register/enterprise")
    public ResponseEntity<AuthResponse> registerEnterprise(@Valid @RequestBody EnterpriseRegisterRequest request) {
        return ResponseEntity.ok(authService.registerEnterprise(request));
    }
}
