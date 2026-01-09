package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.DemoUserCreationRequest;
import com.handgrow.demo.dto.response.DemoUserResponse;

public interface DemoUserService {
    DemoUserResponse demoCreateUser(DemoUserCreationRequest request);

    DemoUserResponse demoGetUserById(Long id);
}
