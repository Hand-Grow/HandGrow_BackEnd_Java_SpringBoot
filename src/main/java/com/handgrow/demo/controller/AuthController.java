package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.*;
import com.handgrow.demo.dto.response.AuthResponse;
import com.handgrow.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/register/farmer")
    public ResponseEntity<AuthResponse> registerFarmer(@RequestBody FarmerRegisterRequest request) {
        return ResponseEntity.ok(authService.registerFarmer(request));
    }

    @PostMapping("/register/coop")
    public ResponseEntity<AuthResponse> registerCoop(@RequestBody CoopRegisterRequest request) {
        return ResponseEntity.ok(authService.registerCoop(request));
    }

    @PostMapping("/register/enterprise")
    public ResponseEntity<AuthResponse> registerEnterprise(@RequestBody EnterpriseRegisterRequest request) {
        return ResponseEntity.ok(authService.registerEnterprise(request));
    }
}
