package com.demo.gateway.config;

import java.util.Date;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.UserNotConfirmedException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtTokenProcessor jwtTokenProcessor;
    private final CognitoConfig cognitoConfig;
    private final AWSCognitoIdentityProvider cognitoIdentityProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        if (authMissing(request)) {
            log.info("No Authorization header found. Sending 404 UNAUTHORIZED error.");
            return sendError(response, HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = getAuthHeader(request);

        if(jwtToken == null){
            log.info("No jwt token found. Sending 404 UNAUTHORIZED error.");
            return sendError(response, HttpStatus.UNAUTHORIZED);
        }
        
        JWTClaimsSet claimsSet = jwtTokenProcessor.decodeToken(jwtToken)
                                                    .orElse(null);

        if(claimsSet == null){
            log.info("Error in decoding jwt, sending 404 UNAUTHORIZED");
            return sendError(response, HttpStatus.UNAUTHORIZED);
        }

        if(!isValid(claimsSet)){
            log.info("Invalid jwt token");
            return sendError(response, HttpStatus.UNAUTHORIZED);    
        }

        if(isExpired(claimsSet)){
            log.info("Jwt token expired");
            return sendError(response, HttpStatus.UNAUTHORIZED);
        }

        String email = claimsSet.getClaim("email").toString();
        String userId = claimsSet.getSubject();

        // Checks whether the user actually exists in Cognito
        if(!userExists(email)){
            log.info("User[{}] doesn't or isn't confirmed.", userId);
            return sendError(response, HttpStatus.UNAUTHORIZED);
        }

        String phoneNumber = claimsSet.getClaim("phone_number").toString();

        
        // "cognito:groups" is a List of the groups in which the user belongs to
        // Here, either the user has an admin role or user role
        @SuppressWarnings("unchecked")
        String role = ((List<String>)claimsSet.getClaims().get("cognito:groups")).get(0);

       
        User user =  User.builder()
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .userId(userId)
                        .role(role)
                        .build();
        
        log.info("User authenticated: {}", user);

        attachUserIdToResponse(request, user);

        return chain.filter(exchange);
    }

    private boolean authMissing(ServerHttpRequest request) {
        List<String> authHeaderList = request.getHeaders().getOrEmpty("Authorization");
    return  authHeaderList.isEmpty() || 
            authHeaderList.get(0) == null || 
            !authHeaderList.get(0).startsWith("Bearer ");
    }
    

    private String getAuthHeader(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getOrEmpty("Authorization").get(0);
        //Bearer <token>
        return authHeader.substring(7);
    }
    
    private Mono<Void> sendError(ServerHttpResponse response, HttpStatus status) {
        response.setStatusCode(status);
        return response.setComplete();
    }

    private void attachUserIdToResponse(ServerHttpRequest request, User user) {
        request.mutate()
                .header("user_id", user.getUserId())
                .header("email", user.getEmail())
                .header("role", user.getRole())
                .build();

    }

    private boolean userExists(String email){
        try{
            AdminGetUserRequest request = new AdminGetUserRequest()
                                                .withUsername(email)
                                                .withUserPoolId(cognitoConfig.getPoolId());
            cognitoIdentityProvider.adminGetUser(request);
            return true;
        }catch(UserNotFoundException | UserNotConfirmedException ex){
            return false;
        }
    }

    private boolean isValid(JWTClaimsSet claimsSet){
        return  jwtTokenProcessor.isCorrectIssuer(claimsSet) &&
                jwtTokenProcessor.isCorrectAudience(claimsSet) && 
                jwtTokenProcessor.isIdToken(claimsSet);
    }

    private boolean isExpired(JWTClaimsSet claimsSet){
        Date expirationTime = claimsSet.getExpirationTime();
        if (expirationTime == null) {
            // If the expiration time claim is not present, consider the token as not expired
            return false;
        }

        Date now = new Date();
        return now.after(expirationTime);
    }

}
