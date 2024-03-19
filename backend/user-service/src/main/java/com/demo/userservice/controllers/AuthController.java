package com.demo.userservice.controllers;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.demo.userservice.dto.ChangePassword;
import com.demo.userservice.dto.ConfirmEmailRequest;
import com.demo.userservice.dto.ForgotPasswordRequest;
import com.demo.userservice.dto.InitiateAuthResponse;
import com.demo.userservice.dto.SignInRequest;
import com.demo.userservice.dto.SignInResponse;
import com.demo.userservice.dto.SignUpRequest;
import com.demo.userservice.dto.SoftwareTokenVerificationRequest;
import com.demo.userservice.dto.TotpCodeRequest;
import com.demo.userservice.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResult> createUser(@Valid @RequestBody SignUpRequest signUpRequest){
        SignUpResult result = userService
                                    .registerUser(signUpRequest.email(), 
                                                signUpRequest.phoneNumber(),
                                                signUpRequest.password());
        return ResponseEntity.status(201).body(result);
    }

    @PostMapping("/confirm_account")
    public ResponseEntity<ConfirmSignUpResult> confirmUser(@Valid @RequestBody ConfirmEmailRequest confirmRequest){
        ConfirmSignUpResult result = userService
                                        .confirmVerificationCode(confirmRequest.userId(), confirmRequest.code());
                                        
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/forgot_password_request")
    public ResponseEntity<ForgotPasswordResult> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest){
        ForgotPasswordResult result = userService
                                        .forgotPassword(forgotPasswordRequest.email());
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/confirm_forgot_password")
    public ResponseEntity<ConfirmForgotPasswordResult> confirmForgotPassword(@Valid @RequestBody ChangePassword changePasswordRequest){
        ConfirmForgotPasswordResult result = userService.confirmForgotPassword(
                                                        changePasswordRequest.email(), 
                                                        changePasswordRequest.password(), 
                                                        changePasswordRequest.code());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@Valid @RequestBody SignInRequest signInRequest){
        InitiateAuthResult result = userService
                                        .signInUser(signInRequest.email(), signInRequest.password());
        
        // first time signing in
        if(result.getChallengeName() == null){
            String accessToken = result.getAuthenticationResult().getAccessToken();
            String secretCode = userService.getSecretCodeForTotpMfa(accessToken);
            /*
             * Response object contains accessToken and secretCode, which will be used later
             * to enable mfa for this current user. secretCode is to be used as a QR 
             * and should be scanned by a authenticator like Google Authenticator.
             * Here, I am creating the url which will be used to generate QR code on the
             * frontedn 
             */
            String secretCodeUrl = String.format("otpauth://totp/%s?secret=%s&issuer=%s",
                                                    signInRequest.email(),
                                                    secretCode,
                                                    "library-management-app");

            InitiateAuthResponse authResponse = InitiateAuthResponse
                                                    .builder()
                                                    .mfaTokens(new InitiateAuthResponse
                                                                    .MfaTokens(accessToken, secretCodeUrl))
                                                    .build();
            return ResponseEntity.ok(authResponse);
        }

        /* If it's not the first sign it, the api will return SOFTWARE_TOKEN_MFA challenge
         * and will give a session string will is to be used for making further calls so that
         * the challenge is completed (the totp code is verified), after which the AuthenticationResults
         * will be populated and will return tokens
         */
        InitiateAuthResponse authResponse = InitiateAuthResponse
                                                    .builder()
                                                    .challengeName(result.getChallengeName())
                                                    .session(result.getSession())
                                                    .build();
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/verify_software_token")
    public ResponseEntity<?> verifySoftwareToken(@RequestBody SoftwareTokenVerificationRequest softwareTokenVerificationRequest){
        userService.verifySoftwareToken(softwareTokenVerificationRequest.accessToken(), softwareTokenVerificationRequest.code());
        userService.setTotpMfaPreference(softwareTokenVerificationRequest.accessToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify_totp_code")
    public ResponseEntity<SignInResponse> verifyTotpToken(@Valid @RequestBody TotpCodeRequest totpCodeRequest){
        var result = userService.respondtoTotpMfaChallenge(totpCodeRequest.session(),totpCodeRequest.email(),totpCodeRequest.code());
        
        GetUserResult userResult = userService.fetchUserInfo(result.getAuthenticationResult().getAccessToken());
        
        Map<String, String> map = userResult.getUserAttributes()
                                            .stream()
                                            .collect(Collectors.toMap(AttributeType::getName, AttributeType::getValue));

        SignInResponse loginResponse = SignInResponse.builder()
                                            .authenticationResults(result.getAuthenticationResult())
                                            .email(map.get("email"))
                                            .phoneNumber(map.get("phone_number"))
                                            .role(map.get("custom:groups"))
                                            .userId(map.get("sub"))
                                            .build();   
                                                                 
        return ResponseEntity.ok(loginResponse);
    }

}
