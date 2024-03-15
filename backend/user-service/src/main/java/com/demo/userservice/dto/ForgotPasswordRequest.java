package com.demo.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
    @NotBlank(message="Email cannot be blank") String email
) {
    
}
