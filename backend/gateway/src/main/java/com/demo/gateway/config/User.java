package com.demo.gateway.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String userId;
    private String email;
    private String phoneNumber;
    private String password;
    private String role;
}
