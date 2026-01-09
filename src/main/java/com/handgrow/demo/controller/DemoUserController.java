package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.DemoUserCreationRequest;
import com.handgrow.demo.dto.response.DemoUserResponse;
import com.handgrow.demo.service.DemoUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class DemoUserController {
    private final DemoUserService demoUserService;

    @PostMapping
    public ResponseEntity<DemoUserResponse> createUser(@RequestBody DemoUserCreationRequest request) {
        return ResponseEntity.ok(demoUserService.demoCreateUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemoUserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(demoUserService.demoGetUserById(id));
    }
}
