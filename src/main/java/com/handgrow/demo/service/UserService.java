package com.handgrow.demo.service;

import com.handgrow.demo.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserProfile(String username);
}
