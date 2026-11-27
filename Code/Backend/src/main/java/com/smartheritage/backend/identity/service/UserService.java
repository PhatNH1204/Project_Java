package com.smartheritage.backend.identity.service;

import com.smartheritage.backend.common.BadRequestException;
import com.smartheritage.backend.common.ConflictException;
import com.smartheritage.backend.common.NotFoundException;
import com.smartheritage.backend.identity.dto.ChangePasswordRequest;
import com.smartheritage.backend.identity.dto.ProfileUpdateRequest;
import com.smartheritage.backend.identity.dto.UserProfileResponse;
import com.smartheritage.backend.identity.entity.UserAccount;
import com.smartheritage.backend.identity.repository.UserAccountRepository;
import com.smartheritage.backend.heritage.repository.SupportedLanguageRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final SupportedLanguageRepository languages;

    public UserService(
            UserAccountRepository users,
            PasswordEncoder passwordEncoder,
            SupportedLanguageRepository languages) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.languages = languages;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse profile(Long userId) {
        return toResponse(findUser(userId));
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        UserAccount user = findUser(userId);
        String username = request.username().trim().toLowerCase(Locale.ROOT);
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByUsernameIgnoreCaseAndIdNot(username, userId)) {
            throw new ConflictException("Username is already in use");
        }
        if (users.existsByEmailIgnoreCaseAndIdNot(email, userId)) {
            throw new ConflictException("Email is already in use");
        }
        String language = request.preferredLanguage().trim().toLowerCase(Locale.ROOT);
        languages.findByCodeIgnoreCaseAndActiveTrue(language)
                .orElseThrow(() -> new NotFoundException("Supported language not found"));
        user.updateProfile(username, email, language);
        return toResponse(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        UserAccount user = findUser(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (request.newPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException("Password must not exceed 72 UTF-8 bytes");
        }
        user.updatePasswordHash(passwordEncoder.encode(request.newPassword()));
    }

    private UserAccount findUser(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public static UserProfileResponse toResponse(UserAccount user) {
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getRole().getName(), user.getPreferredLanguage(), user.isActive());
    }
}
