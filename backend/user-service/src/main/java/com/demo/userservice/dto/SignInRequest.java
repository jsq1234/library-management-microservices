package com.demo.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
    @NotBlank(message="Email cannot be blank") String email,
    @NotBlank(message="Password cannot be blank") String password
) {
    
}