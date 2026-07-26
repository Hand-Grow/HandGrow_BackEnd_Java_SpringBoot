package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.*;
import com.handgrow.demo.dto.response.AuthResponse;
import com.handgrow.demo.entity.*;
import com.handgrow.demo.exception.AppException;
import com.handgrow.demo.exception.ErrorCode;
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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        Account account = accountRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String accessToken =
                jwtUtil.generateToken(account.getUsername(), account.getRole().getName());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken("refresh_token_placeholder")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerFarmer(FarmerRegisterRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        Role role = roleRepository
                .findByName("FARMER")
                .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_SERVER_ERROR));

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        Farmer farmer = Farmer.builder()
                .account(account)
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .produce(request.getProduce())
                .build();

        farmerRepository.save(farmer);

        String accessToken = jwtUtil.generateToken(account.getUsername(), role.getName());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken("refresh_token_placeholder")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerCoop(CoopRegisterRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        Role role =
                roleRepository.findByName("COOP").orElseThrow(() -> new AppException(ErrorCode.INTERNAL_SERVER_ERROR));

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        Cooperative coop = Cooperative.builder()
                .account(account)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .commune(request.getCommune())
                .province(request.getProvince())
                .produce(request.getProduce())
                .build();

        cooperativeRepository.save(coop);

        String accessToken = jwtUtil.generateToken(account.getUsername(), role.getName());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken("refresh_token_placeholder")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerEnterprise(EnterpriseRegisterRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        Role role = roleRepository
                .findByName("ENTERPRISE")
                .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_SERVER_ERROR));

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        Enterprise enterprise = Enterprise.builder()
                .account(account)
                .companyName(request.getCompanyName())
                .phoneNumber(request.getPhoneNumber())
                .commune(request.getCommune())
                .province(request.getProvince())
                .build();

        enterpriseRepository.save(enterprise);

        String accessToken = jwtUtil.generateToken(account.getUsername(), role.getName());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken("refresh_token_placeholder")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build();
    }
}
