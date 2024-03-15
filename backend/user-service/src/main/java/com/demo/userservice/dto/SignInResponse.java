package com.demo.userservice.dto;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class SignInResponse {
    private AuthenticationResultType authenticationResults;
    private String userId;
    private String email;
    private String role;
    private String phoneNumber;
}