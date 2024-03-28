package com.demo.userservice.services;

import org.springframework.stereotype.Service;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import com.amazonaws.services.cognitoidp.model.AdminConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.AdminConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserResult;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AssociateSoftwareTokenRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
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
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.SetUserMFAPreferenceRequest;
import com.amazonaws.services.cognitoidp.model.SetUserMFAPreferenceResult;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.amazonaws.services.cognitoidp.model.SoftwareTokenMfaSettingsType;
import com.amazonaws.services.cognitoidp.model.VerifySoftwareTokenRequest;
import com.amazonaws.services.cognitoidp.model.VerifySoftwareTokenResult;
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
       String secretHash = calculateSecretHash(email);

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
        String secretHash = calculateSecretHash(userId);

        log.info("Confirming user[{}]", userId);

        ConfirmSignUpRequest request = 
                    new ConfirmSignUpRequest()
                        .withClientId(cognitoConfig.getClientId())
                        .withConfirmationCode(code)
                        .withSecretHash(secretHash)
                        .withUsername(userId);

        ConfirmSignUpResult confirmationResult = cognitoIdentityProvider.confirmSignUp(request);

        log.info("User[{}] successfully confirmed: {}", userId, confirmationResult);

        return confirmationResult;
    }

    public InitiateAuthResult signInUser(String email, String password) {
        String secretHash = calculateSecretHash(email);
        
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

    public VerifySoftwareTokenResult verifySoftwareToken(String accessToken, String totpCode){
        var verifySoftwareTokenRequest = new VerifySoftwareTokenRequest()
                                                .withAccessToken(accessToken)
                                                .withUserCode(totpCode)
                                                .withFriendlyDeviceName("totpCode");
        var result = cognitoIdentityProvider.verifySoftwareToken(verifySoftwareTokenRequest);
        
        log.info("Totp code verified: {}", result);

        return result;
    }

    public SetUserMFAPreferenceResult setTotpMfaPreference(String accessToken){
        SetUserMFAPreferenceRequest setUserMFAPreferenceRequest = new SetUserMFAPreferenceRequest()
                                                    .withAccessToken(accessToken)
                                                    .withSoftwareTokenMfaSettings(
                                                        new SoftwareTokenMfaSettingsType()
                                                                .withEnabled(true)
                                                                .withPreferredMfa(true)     
                                                    );

        SetUserMFAPreferenceResult result = cognitoIdentityProvider.setUserMFAPreference(setUserMFAPreferenceRequest);
        
        log.info("User will be prompted for TOTP authentication from now on");

        return result;
    }

    public RespondToAuthChallengeResult respondtoTotpMfaChallenge(String session, String email, String code){
        String secretHash = calculateSecretHash(email);

        var authChallengeRequest = new RespondToAuthChallengeRequest()
                                            .withClientId(cognitoConfig.getClientId())
                                            .withSession(session)
                                            .withChallengeName(ChallengeNameType.SOFTWARE_TOKEN_MFA)
                                            .withChallengeResponses(Map.ofEntries(
                                                Map.entry("USERNAME", email),
                                                Map.entry("SOFTWARE_TOKEN_MFA_CODE", code),
                                                Map.entry("SECRET_HASH", secretHash)
                                            ));
        var authChallengeResult = cognitoIdentityProvider.respondToAuthChallenge(authChallengeRequest);

        log.info("Mfa challenge result: {}", authChallengeResult);

        return authChallengeResult; 
    }

    public String getSecretCodeForTotpMfa(String accessToken){
        var associateSoftwareTokenRequest = new AssociateSoftwareTokenRequest()
                                                    .withAccessToken(accessToken);
        return cognitoIdentityProvider.associateSoftwareToken(associateSoftwareTokenRequest).getSecretCode();
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


    public AdminAddUserToGroupResult addUserToUserGroup(String userId){
        
        AdminAddUserToGroupRequest request = new AdminAddUserToGroupRequest()
                                                    .withGroupName("user")
                                                    .withUserPoolId(cognitoConfig.getPoolId())
                                                    .withUsername(userId);
        
        log.info("Adding user to User group");

        AdminAddUserToGroupResult result = cognitoIdentityProvider.adminAddUserToGroup(request);

        log.info("Added user to User group: {}", result);

        return result;
    }

    public ForgotPasswordResult forgotPassword(String email){
        String secretHash = calculateSecretHash(email);

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
        String secretHash = calculateSecretHash(email);

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
        String secretHash = calculateSecretHash(userId);

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

    public AdminConfirmSignUpResult adminConfirmUserSignUp(String userId){
        AdminConfirmSignUpResult result = cognitoIdentityProvider.adminConfirmSignUp(
            new AdminConfirmSignUpRequest()
                .withUserPoolId(cognitoConfig.getPoolId())
                .withUsername(userId)
        );

        if(result.getSdkHttpMetadata().getHttpStatusCode() == 200){
            log.info("User[{}] confirmed", userId);
        }else{
            log.info("User couldn't be confirmed.");
        }

        return result;
    }

    public AdminDeleteUserResult adminDeleteUserResult(String userId){
        AdminDeleteUserResult result = cognitoIdentityProvider.adminDeleteUser(
            new AdminDeleteUserRequest()
                .withUserPoolId(cognitoConfig.getPoolId())
                .withUsername(userId)
        );

        if(result.getSdkHttpMetadata().getHttpStatusCode() == 200){
            log.info("User[{}] deleted.", userId);
        }else{
            log.info("User couldn't be deleted.");
        }
        return result;
    }

    private String calculateSecretHash(String username) {
        return SecretHashUtil.calculateSecretHash(
                cognitoConfig.getClientId(),
                cognitoConfig.getClientSecret(),
                username
            );
    }
}
