package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.DemoUserCreationRequest;
import com.handgrow.demo.dto.response.DemoUserResponse;
import com.handgrow.demo.repository.DemoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// Best practice is to use Constructor Injection for better testability and immutability
// In this case, @RequiredArgsConstructor from Lombok generates a constructor with required arguments
public class DemoUserServiceImpl implements DemoUserService {
    private final DemoUserRepository demoUserRepository;

    @Override
    public DemoUserResponse demoCreateUser(DemoUserCreationRequest request) {
        return null;
    }

    @Override
    public DemoUserResponse demoGetUserById(Long id) {
        return null;
    }
}
