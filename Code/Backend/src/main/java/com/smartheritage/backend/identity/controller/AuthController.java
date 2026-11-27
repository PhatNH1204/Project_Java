package com.smartheritage.backend.identity.controller;

import com.smartheritage.backend.common.ApiResponse;
import com.smartheritage.backend.identity.dto.AuthResponse;
import com.smartheritage.backend.identity.dto.LoginRequest;
import com.smartheritage.backend.identity.dto.RegisterRequest;
import com.smartheritage.backend.identity.service.AuthService;
import com.smartheritage.backend.identity.entity.RevokedToken;
import com.smartheritage.backend.identity.repository.RevokedTokenRepository;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RevokedTokenRepository revokedTokens;

    public AuthController(AuthService authService, RevokedTokenRepository revokedTokens) {
        this.authService = authService;
        this.revokedTokens = revokedTokens;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noStore())
                .body(ApiResponse.of(authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(ApiResponse.of(authService.login(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        revokedTokens.save(new RevokedToken(jwt.getId(), jwt.getExpiresAt()));
        return ResponseEntity.noContent().build();
    }
}
