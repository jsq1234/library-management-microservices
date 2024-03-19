package com.demo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiateAuthResponse {

    private String challengeName;
    private String session;
    private MfaTokens mfaTokens;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MfaTokens {
        private String accessToken;
        private String secretCodeUrl;
    }
}
