package com.demo.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record TotpCodeRequest(
    @NotBlank(message="session cannot be") String session,
    @NotBlank(message="email cannot be") String email,
    @NotBlank(message="code cannot be") String code
) {
    
}
