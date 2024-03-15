package com.demo.userservice.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Getter
@Configuration
public class CognitoConfig {
    @Value("${cognito.pool_id}")
    private String poolId;

    @Value("${cognito.client_id}")
    private String clientId;

    @Value("${cognito.client_secret}")
    private String clientSecret;
    
    @Value("${cognito.region}")
    private String region;

    @Value("${aws.access_key}")
    private String accessKey;

    @Value("${aws.secret_access_key}")
    private String secretAccessKey;

    private String issuer;

    @PostConstruct
    void init(){
        issuer = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, poolId);
    }

    @Bean
    public AWSCognitoIdentityProvider awsCognitoIdentityProvider() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretAccessKey);
        return AWSCognitoIdentityProviderClient.builder()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.fromName(region))
                    .build();
    }
}

