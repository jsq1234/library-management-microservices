package com.demo.userservice.services;

import org.springframework.stereotype.Service;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.demo.userservice.config.CognitoConfig;
import com.demo.userservice.util.SecretHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final AWSCognitoIdentityProvider cognitoIdentityProvider;
    private final CognitoConfig cognitoConfig;

    public SignUpResult registerUser(String email, String phoneNumber, String password) {
       String secretHash = SecretHashUtil.calculateSecretHash(
                                        cognitoConfig.getClientId(), 
                                        cognitoConfig.getClientSecret(),
                                        email);

        log.info("Creating new user: {}", email);

        SignUpRequest signUpRequest = 
                    new SignUpRequest()
                                .withClientId(cognitoConfig.getClientId())
                                .withUsername(email)
                                .withPassword(password)
                                .withSecretHash(secretHash)
                                .withUserAttributes(
                                    new AttributeType[]{
                                        new AttributeType()
                                            .withName("email")
                                            .withValue(email),
                                        new AttributeType()
                                            .withName("phone_number")
                                            .withValue(phoneNumber),
                                        new AttributeType()
                                            .withName("custom:groups")
                                            .withValue("user")
                                    }
                                );
        
        SignUpResult signUpResult = cognitoIdentityProvider.signUp(signUpRequest);
        
        log.info("User succesfully created : {}", signUpResult);

        return signUpResult;
    }

    public ConfirmSignUpResult confirmVerificationCode(String userId, String code) {
        String secretHash = SecretHashUtil.calculateSecretHash(
                                        cognitoConfig.getClientId(), 
                                        cognitoConfig.getClientSecret(),
                                        userId);

        log.info("Confirming user[{}]", userId);

        ConfirmSignUpRequest request = 
                    new ConfirmSignUpRequest()
                        .withClientId(cognitoConfig.getClientId())
                        .withConfirmationCode(code)
                        .withSecretHash(secretHash)
                        .withUsername(userId);

        ConfirmSignUpResult confirmationResult = cognitoIdentityProvider.confirmSignUp(request);

        log.info("User[{}] successfully confirmed: {}", userId, confirmationResult);

        log.info("Adding user to User group");

        var result = addUserToUserGroup(userId);

        log.info("Added user to User group: {}", result);

        return confirmationResult;
    }

    public InitiateAuthResult signInUser(String email, String password) {
        String secretHash = SecretHashUtil.calculateSecretHash(
                                        cognitoConfig.getClientId(), 
                                        cognitoConfig.getClientSecret(),
                                        email);
        
        Map<String,String> authParams = Map.ofEntries(
            Map.entry("USERNAME", email),
            Map.entry("PASSWORD", password),
            Map.entry("SECRET_HASH", secretHash)
        );

        log.info("Signing in user[{}]", email);

        InitiateAuthRequest authRequest = new InitiateAuthRequest()
                                                .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
                                                .withAuthParameters(authParams)
                                                .withClientId(cognitoConfig.getClientId());
        
        
        InitiateAuthResult result = cognitoIdentityProvider.initiateAuth(authRequest);

        log.info("User signed in: {}", result);

        return result;
    }

    public GetUserResult fetchUserInfo(String accessToken){
        GetUserRequest request = new GetUserRequest()
                                    .withAccessToken(accessToken);

        GetUserResult result = cognitoIdentityProvider.getUser(request);
        return result;
    }

    public AdminGetUserResult fetchUserInfoByEmail(String email){
        AdminGetUserRequest request = new AdminGetUserRequest()
                                            .withUserPoolId(cognitoConfig.getPoolId())
                                            .withUsername(email);
        
        AdminGetUserResult result =  cognitoIdentityProvider.adminGetUser(request);
        
        return result;
    }


    public AdminAddUserToGroupResult addUserToUserGroup(String email){
        AdminAddUserToGroupRequest request = new AdminAddUserToGroupRequest()
                                                .withGroupName("user")
                                                .withUserPoolId(cognitoConfig.getPoolId())
                                                .withUsername(email);
        AdminAddUserToGroupResult result = cognitoIdentityProvider.adminAddUserToGroup(request);

        return result;
    }

    public ForgotPasswordResult forgotPassword(String email){
        String secretHash = SecretHashUtil.calculateSecretHash(
                                        cognitoConfig.getClientId(), 
                                        cognitoConfig.getClientSecret(),
                                        email);
        log.info("Sending password change request to {}", email);
                            
        ForgotPasswordRequest request = new ForgotPasswordRequest()
                                                .withClientId(cognitoConfig.getClientId())
                                                .withUsername(email)
                                                .withSecretHash(secretHash);

        ForgotPasswordResult result = cognitoIdentityProvider.forgotPassword(request);

        log.info("{}", result);

        return result;

    }

    public ConfirmForgotPasswordResult confirmForgotPassword(String email, String password, String code){
        String secretHash = SecretHashUtil.calculateSecretHash(
                                        cognitoConfig.getClientId(), 
                                        cognitoConfig.getClientSecret(),
                                        email);

        ConfirmForgotPasswordRequest request = new ConfirmForgotPasswordRequest()
                                                        .withClientId(cognitoConfig.getClientId())
                                                        .withConfirmationCode(code)
                                                        .withUsername(email)
                                                        .withSecretHash(secretHash)
                                                        .withPassword(password);
        
        ConfirmForgotPasswordResult result = cognitoIdentityProvider.confirmForgotPassword(request);

        log.info("{}", result);

        return result;
    }
    public AdminInitiateAuthResult renewTokens(String refreshToken, String userId) {
        String secretHash = SecretHashUtil.calculateSecretHash(
                                        cognitoConfig.getClientId(), 
                                        cognitoConfig.getClientSecret(),
                                        userId);

        AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                                                    .withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                                                    .withClientId(cognitoConfig.getClientId())
                                                    .withUserPoolId(cognitoConfig.getPoolId())
                                                    .withAuthParameters(
                                                        Map.of(
                                                            "REFRESH_TOKEN", refreshToken,
                                                            "SECRET_HASH", secretHash
                                                        )
                                                    );

        log.info("Initiating auth request: {}", authRequest);
        
        AdminInitiateAuthResult authResult = cognitoIdentityProvider.adminInitiateAuth(authRequest);

        log.info("{}", authResult);
        
        return authResult;
    }
}
