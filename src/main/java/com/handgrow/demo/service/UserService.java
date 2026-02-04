package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.FarmerLocationUpdateDto;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserProfile(String username);

    SimpleResponse updateFarmerLocation(String username, FarmerLocationUpdateDto locationDto);
}
