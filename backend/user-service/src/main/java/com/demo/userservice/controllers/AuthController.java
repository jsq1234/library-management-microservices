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
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.demo.userservice.dto.ConfirmEmailRequest;
import com.demo.userservice.dto.SignInRequest;
import com.demo.userservice.dto.SignInResponse;
import com.demo.userservice.dto.SignUpRequest;
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

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmSignUpResult> confirmUser(@Valid @RequestBody ConfirmEmailRequest confirmRequest){
        ConfirmSignUpResult result = userService
                                        .confirmVerificationCode(confirmRequest.userId(), confirmRequest.code());
                                        
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> loginUser(@Valid @RequestBody SignInRequest signInRequest){
        InitiateAuthResult result = userService
                                        .signInUser(signInRequest.email(), signInRequest.password());
        
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
