package com.demo.userservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.CodeDeliveryDetailsType;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.demo.userservice.config.CognitoConfig;
import com.demo.userservice.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {
    @Mock
    private AWSCognitoIdentityProvider cognitoIdentityProvider;

    @InjectMocks
    private UserService userService;

    @Mock
    private CognitoConfig cognitoConfig;



    @Test
    void testRegisterUser(){
        when(cognitoConfig.getClientId()).thenReturn("testClientId");
        when(cognitoConfig.getClientSecret()).thenReturn("testClientSecret");

        // Mock input values
        String email = "test@example.com";
        String phoneNumber = "+918888888888";
        String password = "Password@1234";
        
        SignUpResult signUpResult = new SignUpResult();
        signUpResult.setUserSub("testUserSub");
        signUpResult.setCodeDeliveryDetails(new CodeDeliveryDetailsType().withDeliveryMedium("EMAIL"));
        signUpResult.setUserConfirmed(false);
        
        when(userService.registerUser(email, phoneNumber, password)).thenReturn(signUpResult);
        
        // Call the method under test
        SignUpResult result = userService.registerUser(email, phoneNumber, password);
        
    
        // Perform assertions
        assertNotNull(result.getUserSub());
        assertNotNull(result);
        assertNotNull(result.getCodeDeliveryDetails());
        assertEquals("EMAIL", result.getCodeDeliveryDetails().getDeliveryMedium());
        assertEquals(false, result.getUserConfirmed());
    }

    @Test
    void testSignInUser(){
        when(cognitoConfig.getClientId()).thenReturn("testClientId");
        when(cognitoConfig.getClientSecret()).thenReturn("testClientSecret");
        // Mock input values
        String email = "test@example.com";
        String password = "Password@1234";
        
        // Mock the behavior of the signInUser method
        InitiateAuthResult initiateAuthResult = new InitiateAuthResult();
        AuthenticationResultType authenticationResult = new AuthenticationResultType();
        authenticationResult.setAccessToken("testAccessToken");
        authenticationResult.setRefreshToken("testRefreshToken");
        authenticationResult.setIdToken("testIdToken");
        initiateAuthResult.setAuthenticationResult(authenticationResult);
        initiateAuthResult.setChallengeName((String) null);

        when(userService.signInUser(email, password)).thenReturn(initiateAuthResult);

        // Call the method under test
        InitiateAuthResult result = userService.signInUser(email, password);

        // Perform assertions
        assertNotNull(result);
        assertNotNull(result.getAuthenticationResult());
        assertNotNull(result.getAuthenticationResult().getAccessToken());
        assertNotNull(result.getAuthenticationResult().getRefreshToken());
        assertNotNull(result.getAuthenticationResult().getIdToken());
        assertNull(result.getChallengeName());

    }

    @Test
    public void testConfirmUser(){
        when(cognitoConfig.getClientId()).thenReturn("testClientId");
        when(cognitoConfig.getClientSecret()).thenReturn("testClientSecret");
        // Mock input values
        String email = "test@example.com";
        String verificationCode = "123456";

        // Mock the behavior of the confirmVerificationCode method
        ConfirmSignUpResult confirmSignUpResult = new ConfirmSignUpResult();

        when(userService.confirmVerificationCode(email, verificationCode)).thenReturn(confirmSignUpResult);

        // Call the method under test
        ConfirmSignUpResult result = userService.confirmVerificationCode(email, verificationCode);
        // Perform assertions
        assertNotNull(result);
    }

    @Test
    public void testAddToUserGroup(){
        String userId = "testUserId";

        AdminAddUserToGroupResult adminAddUserToGroupResult = new AdminAddUserToGroupResult();
        // Mock the behavior of the addUserToUserGroup method
        when(userService.addUserToUserGroup(userId)).thenReturn(adminAddUserToGroupResult);
        
        // Call the method under test
        AdminAddUserToGroupResult result = userService.addUserToUserGroup(userId);
        // Perform assertions
        assertNotNull(result);
    }
}
