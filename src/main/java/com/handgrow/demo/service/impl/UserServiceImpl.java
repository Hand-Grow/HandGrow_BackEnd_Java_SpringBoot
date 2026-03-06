package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.FarmerLocationUpdateDto;
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
                .address(coop.getAddress())
                .commune(coop.getCommune())
                .province(coop.getProvince())
                .produce(coop.getProduce() != null ? coop.getProduce().name() : null)
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
                .address(enterprise.getAddress())
                .commune(enterprise.getCommune())
                .province(enterprise.getProvince())
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
}
