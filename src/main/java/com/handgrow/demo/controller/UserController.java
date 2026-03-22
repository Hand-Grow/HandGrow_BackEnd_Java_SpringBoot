package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.FarmerLocationUpdateDto;
import com.handgrow.demo.dto.request.UpdateProfileRequest;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.dto.response.UserResponse;
import com.handgrow.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponse userResponse = userService.getUserProfile(username);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/location")
    public ResponseEntity<SimpleResponse> updateFarmerLocation(
            Authentication authentication, @RequestBody FarmerLocationUpdateDto locationDto) {
        String username = authentication.getName();
        SimpleResponse response = userService.updateFarmerLocation(username, locationDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<SimpleResponse> updateProfile(
            Authentication authentication, @RequestBody UpdateProfileRequest request) {
        String username = authentication.getName();
        SimpleResponse response = userService.updateUserProfile(username, request);
        return ResponseEntity.ok(response);
    }
}
