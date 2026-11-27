package com.smartheritage.backend.identity.security;

import com.smartheritage.backend.identity.entity.UserAccount;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private final JwtEncoder encoder;
    private final Duration lifetime;

    public JwtTokenService(
            JwtEncoder encoder,
            @Value("${app.security.token-minutes}") long tokenMinutes) {
        this.encoder = encoder;
        if (tokenMinutes < 1) {
            throw new IllegalStateException("JWT_TOKEN_MINUTES must be greater than zero");
        }
        this.lifetime = Duration.ofMinutes(tokenMinutes);
    }

    public IssuedToken issue(UserAccount user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("smart-heritage")
                .issuedAt(now)
                .expiresAt(now.plus(lifetime))
                .subject(user.getId().toString())
                .id(UUID.randomUUID().toString())
                .claim("username", user.getUsername())
                .claim("roles", new String[]{"ROLE_" + user.getRole().getName()})
                .build();
        String value = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new IssuedToken(value, lifetime.toSeconds());
    }

    public record IssuedToken(String value, long expiresInSeconds) {
    }
}
