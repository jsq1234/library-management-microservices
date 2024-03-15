package com.demo.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
    @NotBlank(message = "Email cannot be blank") String email,
    @NotBlank(message = "Phone number cannot be blank") String phoneNumber,
    @NotBlank(message = "password cannot be blank") String password
) {
    
}
