package com.smartheritage.backend.identity.controller;

import com.smartheritage.backend.common.ApiResponse;
import com.smartheritage.backend.identity.dto.ChangePasswordRequest;
import com.smartheritage.backend.identity.dto.ProfileUpdateRequest;
import com.smartheritage.backend.identity.dto.UserProfileResponse;
import com.smartheritage.backend.identity.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<UserProfileResponse> profile(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.of(userService.profile(userId(jwt)));
    }

    @PutMapping
    public ApiResponse<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.of(userService.updateProfile(userId(jwt), request));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId(jwt), request);
        return ResponseEntity.noContent().build();
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
