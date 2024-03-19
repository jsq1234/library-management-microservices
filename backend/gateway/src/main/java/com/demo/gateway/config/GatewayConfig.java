package com.demo.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-signin-route",
                        r -> r.path("/auth/signin")
                                .uri("http://user-service:8081/"))
                .route("user-signup-route",
                        r -> r.path("/auth/signup")
                                .uri("http://user-service:8081/"))
                .route("user-confirm-route",
                        r -> r.path("/auth/confirm_account")
                                .uri("http://user-service:8081/"))
                .route("user-change-password-request",
                        r -> r.path("/auth/forgot_password_request")
                                .uri("http://user-service:8081/"))
                .route("user-confirm-password-request",
                        r -> r.path("/auth/confirm_forgot_password")
                                .uri("http://user-service:8081/"))
                .route("user-mfa-preferences-route",
                        r -> r.path("/auth/mfaPreferences/**")
                                .uri("http://user-service:8081/"))
                .route("user-verify-software-token",
                        r -> r.path("/auth/verify_software_token")
                                .uri("http://user-service:8081/"))
                .route("user-verify-totp_code",
                        r -> r.path("/auth/verify_totp_code")
                                .uri("http://user-service:8081/"))
                .route("generic-user-routes",
                        r -> r.path("/user/**")
                                .filters(f -> f.filter(jwtAuthenticationFilter))
                                .uri("http://user-service:8081/"))
                .route("book-service-api",
                        r -> r.path("/books/**")
                                .filters(f -> f.filter(jwtAuthenticationFilter))
                                .uri("http://book-service:8082/"))
                .build();
    }
}
