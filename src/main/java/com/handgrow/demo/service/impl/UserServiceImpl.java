package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.FarmerLocationUpdateDto;
import com.handgrow.demo.dto.request.UpdateProfileRequest;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.dto.response.UserResponse;
import com.handgrow.demo.entity.*;
import com.handgrow.demo.repository.*;
import com.handgrow.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AccountRepository accountRepository;
    private final FarmerRepository farmerRepository;
    private final CooperativeRepository cooperativeRepository;
    private final EnterpriseRepository enterpriseRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(String username) {
        Account account =
                accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        String roleName = account.getRole().getName();

        switch (roleName) {
            case "FARMER":
                return buildFarmerResponse(account);
            case "COOP":
                return buildCoopResponse(account);
            case "ENTERPRISE":
                return buildEnterpriseResponse(account);
            default:
                throw new RuntimeException("Unknown user role");
        }
    }

    private UserResponse buildFarmerResponse(Account account) {
        Farmer farmer =
                farmerRepository.findByAccount(account).orElseThrow(() -> new RuntimeException("Farmer not found"));

        String cooperativeId = null;
        String cooperativeName = null;
        if (farmer.getCooperative() != null) {
            cooperativeId = farmer.getCooperative().getId().toString();
            cooperativeName = farmer.getCooperative().getName();
        }

        return UserResponse.builder()
                .id(farmer.getId().toString())
                .fullName(farmer.getFullName())
                .username(account.getUsername())
                .phoneNumber(farmer.getPhoneNumber())
                .role("FARMER")
                .avatarUrl(farmer.getAvatarUrl())
                .address(farmer.getAddress())
                .commune(farmer.getCommune())
                .province(farmer.getProvince())
                .produce(farmer.getProduce() != null ? farmer.getProduce().name() : null)
                .cooperativeId(cooperativeId)
                .cooperativeName(cooperativeName)
                .build();
    }

    private UserResponse buildCoopResponse(Account account) {
        Cooperative coop = cooperativeRepository
                .findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Cooperative not found"));

        return UserResponse.builder()
                .id(coop.getId().toString())
                .fullName(coop.getName())
                .username(account.getUsername())
                .phoneNumber(coop.getPhoneNumber())
                .role("COOP")
                .avatarUrl(coop.getAvatarUrl())
                .address(coop.getAddress())
                .commune(coop.getCommune())
                .province(coop.getProvince())
                .representativeName(coop.getRepresentativeName())
                .build();
    }

    private UserResponse buildEnterpriseResponse(Account account) {
        Enterprise enterprise = enterpriseRepository
                .findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Enterprise not found"));

        return UserResponse.builder()
                .id(enterprise.getId().toString())
                .fullName(enterprise.getCompanyName())
                .username(account.getUsername())
                .phoneNumber(enterprise.getPhoneNumber())
                .role("ENTERPRISE")
                .avatarUrl(enterprise.getAvatarUrl())
                .address(enterprise.getAddress())
                .commune(enterprise.getCommune())
                .province(enterprise.getProvince())
                .representativeName(enterprise.getRepresentativeName())
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse updateFarmerLocation(String username, FarmerLocationUpdateDto locationDto) {
        Account account =
                accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        if (!"FARMER".equals(account.getRole().getName())) {
            throw new RuntimeException("Only farmers can update location");
        }

        Farmer farmer = farmerRepository
                .findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found"));

        farmer.setCommune(locationDto.getCommune());
        farmer.setProvince(locationDto.getProvince());

        farmerRepository.save(farmer);

        return SimpleResponse.builder()
                .success(true)
                .message("Location updated successfully")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse updateUserProfile(String username, UpdateProfileRequest request) {
        Account account =
                accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        String role = account.getRole().getName();

        switch (role) {
            case "FARMER":
                Farmer farmer = farmerRepository
                        .findByAccount(account)
                        .orElseThrow(() -> new RuntimeException("Farmer profile not found"));

                if (request.getFullName() != null) farmer.setFullName(request.getFullName());
                if (request.getPhoneNumber() != null) farmer.setPhoneNumber(request.getPhoneNumber());
                if (request.getAddress() != null) farmer.setAddress(request.getAddress());
                if (request.getCommune() != null) farmer.setCommune(request.getCommune());
                if (request.getProvince() != null) farmer.setProvince(request.getProvince());
                if (request.getAvatarUrl() != null) farmer.setAvatarUrl(request.getAvatarUrl());

                farmerRepository.save(farmer);
                return SimpleResponse.builder()
                        .success(true)
                        .message("Farmer profile updated")
                        .build();

            case "COOP":
                Cooperative coop = cooperativeRepository
                        .findByAccount(account)
                        .orElseThrow(() -> new RuntimeException("Cooperative not found"));

                if (request.getCompanyName() != null) coop.setName(request.getCompanyName());
                if (request.getPhoneNumber() != null) coop.setPhoneNumber(request.getPhoneNumber());
                if (request.getAddress() != null) coop.setAddress(request.getAddress());
                if (request.getCommune() != null) coop.setCommune(request.getCommune());
                if (request.getProvince() != null) coop.setProvince(request.getProvince());
                if (request.getAvatarUrl() != null) coop.setAvatarUrl(request.getAvatarUrl());
                if (request.getRepresentativeName() != null)
                    coop.setRepresentativeName(request.getRepresentativeName());

                cooperativeRepository.save(coop);
                return SimpleResponse.builder()
                        .success(true)
                        .message("Cooperative profile updated")
                        .build();

            case "ENTERPRISE":
                Enterprise enterprise = enterpriseRepository
                        .findByAccount(account)
                        .orElseThrow(() -> new RuntimeException("Enterprise not found"));

                if (request.getCompanyName() != null) enterprise.setCompanyName(request.getCompanyName());
                if (request.getPhoneNumber() != null) enterprise.setPhoneNumber(request.getPhoneNumber());
                if (request.getAddress() != null) enterprise.setAddress(request.getAddress());
                if (request.getCommune() != null) enterprise.setCommune(request.getCommune());
                if (request.getProvince() != null) enterprise.setProvince(request.getProvince());
                if (request.getAvatarUrl() != null) enterprise.setAvatarUrl(request.getAvatarUrl());
                if (request.getRepresentativeName() != null)
                    enterprise.setRepresentativeName(request.getRepresentativeName());
                if (request.getContactEmail() != null) enterprise.setContactEmail(request.getContactEmail());

                enterpriseRepository.save(enterprise);
                return SimpleResponse.builder()
                        .success(true)
                        .message("Enterprise profile updated")
                        .build();

            default:
                throw new RuntimeException("Unsupported role");
        }
    }
}
