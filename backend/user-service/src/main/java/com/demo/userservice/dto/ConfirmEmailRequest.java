package com.demo.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmEmailRequest(
    @NotBlank(message="UserId cannot be blank") String userId,
    @NotBlank(message="Confirmation code cannot be blank") String code
) {
    
}