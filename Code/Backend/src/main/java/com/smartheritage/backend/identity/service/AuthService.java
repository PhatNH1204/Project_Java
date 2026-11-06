package com.smartheritage.backend.identity.service;

import com.smartheritage.backend.common.ConflictException;
import com.smartheritage.backend.common.BadRequestException;
import com.smartheritage.backend.common.InvalidCredentialsException;
import com.smartheritage.backend.identity.dto.AuthResponse;
import com.smartheritage.backend.identity.dto.LoginRequest;
import com.smartheritage.backend.identity.dto.RegisterRequest;
import com.smartheritage.backend.identity.entity.Role;
import com.smartheritage.backend.identity.entity.UserAccount;
import com.smartheritage.backend.identity.repository.RoleRepository;
import com.smartheritage.backend.identity.repository.UserAccountRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserAccountRepository users,
            RoleRepository roles,
            PasswordEncoder passwordEncoder) {
        this.users = users;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim().toLowerCase(Locale.ROOT);
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("Username is already in use");
        }
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email is already in use");
        }
        validatePasswordLength(request.password());
        Role visitorRole = roles.findByNameIgnoreCase("VISITOR")
                .orElseThrow(() -> new IllegalStateException("VISITOR role is missing from the database"));
        UserAccount user = users.save(new UserAccount(
                username, email, passwordEncoder.encode(request.password()),
                request.languageCode().trim().toLowerCase(Locale.ROOT), visitorRole));
        return toResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new InvalidCredentialsException();
        }
        String identifier = request.identifier().trim().toLowerCase(Locale.ROOT);
        UserAccount user = users.findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier)
                .filter(UserAccount::isActive)
                .filter(account -> passwordEncoder.matches(request.password(), account.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return toResponse(user);
    }

    private void validatePasswordLength(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException("Password must not exceed 72 UTF-8 bytes");
        }
    }

    private AuthResponse toResponse(UserAccount user) {
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getRole().getName(), user.getPreferredLanguage());
    }
}
