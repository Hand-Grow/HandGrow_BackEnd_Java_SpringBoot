package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.*;
import com.handgrow.demo.dto.response.AuthResponse;
import com.handgrow.demo.dto.response.UserResponse;
import com.handgrow.demo.entity.*;
import com.handgrow.demo.repository.*;
import com.handgrow.demo.service.AuthService;
import com.handgrow.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AccountRepository accountRepository;
    private final FarmerRepository farmerRepository;
    private final CooperativeRepository cooperativeRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        Account account = accountRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String accessToken = jwtUtil.generateToken(account.getUsername());
        
        UserResponse userResponse = buildUserResponse(account);
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken("refresh_token_placeholder")
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .user(userResponse)
            .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        throw new RuntimeException("Use specific role registration endpoints");
    }

    @Override
    @Transactional
    public AuthResponse registerFarmer(FarmerRegisterRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        Role role = roleRepository.findByName("FARMER")
            .orElseThrow(() -> new RuntimeException("Role not found"));
        
        Account account = Account.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(role)
            .build();
        
        Farmer farmer = Farmer.builder()
            .account(account)
            .fullName(request.getFullName())
            .phoneNumber(request.getPhoneNumber())
            .address(request.getAddress())
            .build();
        
        farmerRepository.save(farmer);
        
        String accessToken = jwtUtil.generateToken(account.getUsername());
        
        UserResponse userResponse = UserResponse.builder()
            .id(farmer.getId().toString())
            .fullName(farmer.getFullName())
            .phoneNumber(farmer.getPhoneNumber())
            .role(role.getName())
            .avatarUrl(farmer.getAvatarUrl())
            .build();
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken("refresh_token_placeholder")
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .user(userResponse)
            .build();
    }

    @Override
    @Transactional
    public AuthResponse registerCoop(CoopRegisterRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        Role role = roleRepository.findByName("COOP")
            .orElseThrow(() -> new RuntimeException("Role not found"));
        
        Account account = Account.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(role)
            .build();
        
        Cooperative coop = Cooperative.builder()
            .account(account)
            .name(request.getName())
            .address(request.getAddress())
            .representativeName(request.getRepresentativeName())
            .build();
        
        cooperativeRepository.save(coop);
        
        String accessToken = jwtUtil.generateToken(account.getUsername());
        
        UserResponse userResponse = UserResponse.builder()
            .id(coop.getId().toString())
            .fullName(coop.getName())
            .phoneNumber(null)
            .role(role.getName())
            .avatarUrl(null)
            .build();
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken("refresh_token_placeholder")
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .user(userResponse)
            .build();
    }

    @Override
    @Transactional
    public AuthResponse registerEnterprise(EnterpriseRegisterRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        Role role = roleRepository.findByName("ENTERPRISE")
            .orElseThrow(() -> new RuntimeException("Role not found"));
        
        Account account = Account.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(role)
            .build();
        
        Enterprise enterprise = Enterprise.builder()
            .account(account)
            .companyName(request.getCompanyName())
            .taxCode(request.getTaxCode())
            .businessType(request.getBusinessType())
            .contactEmail(request.getContactEmail())
            .websiteUrl(request.getWebsiteUrl())
            .address(request.getAddress())
            .enterpriseType(request.getEnterpriseType())
            .name(request.getName())
            .phoneNumber(request.getPhoneNumber())
            .representativeName(request.getRepresentativeName())
            .build();
        
        enterpriseRepository.save(enterprise);
        
        String accessToken = jwtUtil.generateToken(account.getUsername());
        
        UserResponse userResponse = UserResponse.builder()
            .id(enterprise.getId().toString())
            .fullName(enterprise.getCompanyName())
            .phoneNumber(enterprise.getPhoneNumber())
            .role(role.getName())
            .avatarUrl(null)
            .build();
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken("refresh_token_placeholder")
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .user(userResponse)
            .build();
    }
    
    private UserResponse buildUserResponse(Account account) {
        String roleName = account.getRole().getName();
        switch (roleName) {
            case "FARMER":
                Farmer farmer = farmerRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
                return UserResponse.builder()
                    .id(farmer.getId().toString())
                    .fullName(farmer.getFullName())
                    .phoneNumber(farmer.getPhoneNumber())
                    .role("FARMER")
                    .avatarUrl(farmer.getAvatarUrl())
                    .build();
            case "COOP":
                Cooperative coop = cooperativeRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Cooperative not found"));
                return UserResponse.builder()
                    .id(coop.getId().toString())
                    .fullName(coop.getName())
                    .phoneNumber(null)
                    .role("COOP")
                    .avatarUrl(null)
                    .build();
            case "ENTERPRISE":
                Enterprise enterprise = enterpriseRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Enterprise not found"));
                return UserResponse.builder()
                    .id(enterprise.getId().toString())
                    .fullName(enterprise.getCompanyName())
                    .phoneNumber(enterprise.getPhoneNumber())
                    .role("ENTERPRISE")
                    .avatarUrl(null)
                    .build();
            default:
                throw new RuntimeException("Unknown user role");
        }
    }
}