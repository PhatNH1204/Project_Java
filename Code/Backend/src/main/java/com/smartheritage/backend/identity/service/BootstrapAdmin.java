package com.smartheritage.backend.identity.service;

import com.smartheritage.backend.identity.entity.UserAccount;
import com.smartheritage.backend.identity.repository.RoleRepository;
import com.smartheritage.backend.identity.repository.UserAccountRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BootstrapAdmin implements ApplicationRunner {

    private final UserAccountRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String email;
    private final String password;

    public BootstrapAdmin(
            UserAccountRepository users,
            RoleRepository roles,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin-username}") String username,
            @Value("${app.bootstrap.admin-email}") String email,
            @Value("${app.bootstrap.admin-password}") String password) {
        this.users = users;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean configured = !username.isBlank() || !email.isBlank() || !password.isBlank();
        if (!configured) {
            return;
        }
        if (username.isBlank() || email.isBlank() || password.length() < 12
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException(
                    "Configure ADMIN_USERNAME, ADMIN_EMAIL and an ADMIN_PASSWORD of 12-72 UTF-8 bytes");
        }

        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        var existing = users.findByUsernameIgnoreCaseOrEmailIgnoreCase(
                normalizedUsername, normalizedEmail);
        if (existing.isPresent()) {
            UserAccount account = existing.get();
            if (account.getUsername().equalsIgnoreCase(normalizedUsername)
                    && account.getEmail().equalsIgnoreCase(normalizedEmail)
                    && account.getRole().getName().equalsIgnoreCase("ADMIN")) {
                return;
            }
            throw new IllegalStateException(
                    "The configured bootstrap administrator conflicts with an existing account");
        }
        var adminRole = roles.findByNameIgnoreCase("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role is missing from the database"));
        users.save(new UserAccount(normalizedUsername, normalizedEmail,
                passwordEncoder.encode(password), "vi", adminRole));
    }
}
