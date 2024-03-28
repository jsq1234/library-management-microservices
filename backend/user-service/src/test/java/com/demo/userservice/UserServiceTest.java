package com.demo.userservice;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.demo.userservice.config.CognitoConfig;
import com.demo.userservice.services.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private AWSCognitoIdentityProvider cognitoIdentityProvider;

    @Autowired
    private CognitoConfig cognitoConfig;

    @Autowired
    private UserService userService;

    private SignUpResult signUpResult;
    private String email;
    private String password;
    private String phoneNumber;

    @BeforeEach
    public void setUp(){
        this.email = "test@example.com";
        this.password = "Password@1234";
        this.phoneNumber = "+918888888888";

        SignUpResult signUpResult = userService.registerUser(email, phoneNumber, password);
        this.signUpResult = signUpResult;

        userService.adminConfirmUserSignUp(this.signUpResult.getUserSub());
    }

    @AfterEach
    public void tearDown(){
        userService.adminDeleteUserResult(this.signUpResult.getUserSub());
    }

    @Test
    public void testRegisterUser() {
        assertNotNull(signUpResult.getUserSub());
        assertNotNull(signUpResult);
        assertNotNull(signUpResult.getCodeDeliveryDetails());
        assertEquals("EMAIL", signUpResult.getCodeDeliveryDetails().getDeliveryMedium());
        assertEquals(false, signUpResult.getUserConfirmed());
    }

    @Test
    public void testSignInUser(){
        InitiateAuthResult initiateAuthResult = userService.signInUser(email, password);
        assertNotNull(initiateAuthResult);
        assertNotNull(initiateAuthResult.getAuthenticationResult());
        assertNotNull(initiateAuthResult.getAuthenticationResult().getAccessToken());
        assertNotNull(initiateAuthResult.getAuthenticationResult().getRefreshToken());
        assertNotNull(initiateAuthResult.getAuthenticationResult().getIdToken());
        assertNull(initiateAuthResult.getChallengeName());
    }


    /**
     * Test case to verify the functionality of fetching user information by email.
     */
    @Test
    public void testFetchUserInfoByEmail(){
        // Fetch user information by email
        AdminGetUserResult adminGetUserResult = userService.fetchUserInfoByEmail(email);
        assertNotNull(adminGetUserResult);
        assertEquals(true, adminGetUserResult.getEnabled());
        assertEquals("CONFIRMED", adminGetUserResult.getUserStatus());
        assertEquals(signUpResult.getUserSub(), adminGetUserResult.getUsername());

        // Convert user attributes to map for easier assertion
        Map<String,String> map = adminGetUserResult.getUserAttributes()
                                                    .stream()
                                                    .collect(Collectors.toMap
                                                    (
                                                        AttributeType::getName, 
                                                        AttributeType::getValue
                                                    ));
        // Assert user attributes
        assertEquals(email, map.get("email"));
        assertEquals(phoneNumber, map.get("phone_number"));
        assertEquals("user", map.get("custom:groups"));
        assertEquals(signUpResult.getUserSub(), map.get("sub"));
    }
}