package com.smartheritage.backend.identity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void visitorCanRegisterAndLogin() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "simple-visitor",
                                  "email": "simple@example.com",
                                  "password": "StrongPassword123",
                                  "languageCode": "vi"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("simple-visitor"))
                .andExpect(jsonPath("$.data.role").value("VISITOR"))
                .andExpect(jsonPath("$.data.passwordHash").doesNotExist());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identifier": "SIMPLE@example.com",
                                  "password": "StrongPassword123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("simple@example.com"));
    }

    @Test
    void registrationRejectsDuplicateEmailAndLoginRejectsWrongPassword() throws Exception {
        String registration = """
                {
                  "username": "duplicate-check",
                  "email": "duplicate-check@example.com",
                  "password": "StrongPassword123",
                  "languageCode": "en"
                }
                """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration.replace(
                                "\"username\": \"duplicate-check\"",
                                "\"username\": \"another-user\"")))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identifier": "duplicate-check@example.com",
                                  "password": "WrongPassword123"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
