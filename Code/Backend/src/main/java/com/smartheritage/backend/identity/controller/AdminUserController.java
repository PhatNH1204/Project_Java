package com.smartheritage.backend.identity.controller;

import com.smartheritage.backend.common.ApiResponse;
import com.smartheritage.backend.common.ConflictException;
import com.smartheritage.backend.common.NotFoundException;
import com.smartheritage.backend.identity.dto.UserProfileResponse;
import com.smartheritage.backend.identity.dto.UserStatusRequest;
import com.smartheritage.backend.identity.repository.UserAccountRepository;
import com.smartheritage.backend.identity.service.UserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserAccountRepository users;

    public AdminUserController(UserAccountRepository users) {
        this.users = users;
    }

    @GetMapping
    public ApiResponse<List<UserProfileResponse>> users() {
        return ApiResponse.of(users.findAllByOrderByIdAsc().stream()
                .map(UserService::toResponse).toList());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        if (Long.valueOf(jwt.getSubject()).equals(id) && !request.active()) {
            throw new ConflictException("An administrator cannot deactivate their own account");
        }
        var user = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setActive(request.active());
        users.save(user);
        return ResponseEntity.noContent().build();
    }
}
