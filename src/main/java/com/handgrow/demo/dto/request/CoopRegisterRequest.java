package com.handgrow.demo.dto.request;

import com.handgrow.demo.entity.enums.Produce;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CoopRegisterRequest {
    @NotBlank(message = "Cooperative name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String username; // email

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Commune is required")
    private String commune; // xã

    @NotBlank(message = "Province is required")
    private String province; // tỉnh

    @NotNull(message = "Produce is required")
    private Produce produce;
}
