package com.demo.gateway.config;

import org.springframework.stereotype.Component;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.net.URL;
import java.text.ParseException;
import java.util.Optional;
import java.util.List;
import java.net.MalformedURLException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProcessor {
    private final CognitoConfig cognitoConfig;
    private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    @PostConstruct
    void init(){
        try{         
            ResourceRetriever resourceRetriever = new DefaultResourceRetriever(0,0);
            URL jwkUrl = new URL(String.format("%s/.well-known/jwks.json", cognitoConfig.getIssuer()));
            RemoteJWKSet<SecurityContext> remoteJWKSet = new RemoteJWKSet<>(jwkUrl, resourceRetriever);
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, remoteJWKSet);
            jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(keySelector);
        }catch(MalformedURLException ex){
            log.info("Malformed URL: {}", ex.getMessage());
        }
    }

    public Optional<JWTClaimsSet> decodeToken(String token){
        try{
            JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
            log.info("Decoded jwt: {}", claimsSet);
            return Optional.of(claimsSet);
        }catch(BadJOSEException | JOSEException | ParseException ex){
            log.error("Jwt decoding error: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public boolean isCorrectIssuer(JWTClaimsSet claimsSet){
        boolean result = claimsSet.getClaim("iss").equals(cognitoConfig.getIssuer());
        if(!result){
            log.info("Issuer {} in JWT token doesn't match cognito idp {}",
                         claimsSet.getClaim("iss"), cognitoConfig.getIssuer());
        }
        return result;
    }

    public boolean isIdToken(JWTClaimsSet claimsSet){
        boolean result = claimsSet.getClaim("token_use").equals("id");
        if(!result){
            log.info("Jwt token isn't an id token, but {}", claimsSet.getClaim("token_use"));
        }
        return result;
    }

    public boolean isCorrectAudience(JWTClaimsSet claimsSet){
        boolean result = ((List<String>)claimsSet.getClaim("aud")).get(0).equals(cognitoConfig.getClientId());
        if(!result){
            log.info("Audience doesn't match, {} != {}", claimsSet.getClaim("aud"), cognitoConfig.getClientId());
        }
        return result;
    }
}

