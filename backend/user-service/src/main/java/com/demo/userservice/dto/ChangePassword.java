package com.demo.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePassword(
    @NotBlank(message = "email cannot be blank") String email,
    @NotBlank(message = "password cannot be blank") String password,
    @NotBlank(message = "confirmation code cannot be blank") String code
) {
    
}
