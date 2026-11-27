package com.smartheritage.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SmartHeritageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void visitorTokenIsAuthorizedAndCanBeRevoked() throws Exception {
        String visitorToken = registerVisitor("profile-visitor");
        mockMvc.perform(get("/api/users/me").header("Authorization", bearer(visitorToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("profile-visitor"));

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", bearer(visitorToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/me").header("Authorization", bearer(visitorToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void visitorCannotCreateAdminContent() throws Exception {
        String visitorToken = registerVisitor("restricted-visitor");
        mockMvc.perform(post("/api/admin/sites")
                        .header("Authorization", bearer(visitorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Restricted site",
                                  "description": "",
                                  "address": "",
                                  "active": true
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void demoArtifactIsAvailableInTheVisitorCatalog() throws Exception {
        mockMvc.perform(get("/api/artifacts")
                        .param("search", "Khuê Văn Các")
                        .param("lang", "vi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].code").value("VMQTG-KHVK-01"))
                .andExpect(jsonPath("$.data[0].content.title").value("Khuê Văn Các"))
                .andExpect(jsonPath("$.data[0].content.imageUrl")
                        .value("https://commons.wikimedia.org/wiki/File:Constellation_of_Literature_pavilion_-_Temple_of_Literature,_Hanoi_-_DSC04553.JPG"));
    }

    @Test
    void demoCatalogIncludesArtifactsFromOtherHeritageSites() throws Exception {
        mockMvc.perform(get("/api/artifacts")
                        .param("search", "Ngọ Môn")
                        .param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].code").value("HUE-NGOMON-01"))
                .andExpect(jsonPath("$.data[0].content.title").value("Ngo Mon Gate"));
    }

    @Test
    void adminCanPublishBeaconContentAndTrackVisitorExperience() throws Exception {
        String adminToken = login("admin", "TestAdminPass123");
        String visitorToken = registerVisitor("museum-visitor");

        MvcResult siteResult = mockMvc.perform(post("/api/admin/sites")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Test heritage site",
                                  "description": "A test site",
                                  "address": "Hanoi",
                                  "latitude": 21.0285,
                                  "longitude": 105.8542,
                                  "active": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long siteId = data(siteResult).path("id").asLong();

        MvcResult artifactResult = mockMvc.perform(post("/api/admin/artifacts")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "siteId": %d,
                                  "code": "ART-TEST-01",
                                  "locationName": "Main gallery",
                                  "latitude": 21.0286,
                                  "longitude": 105.8543,
                                  "active": true,
                                  "languageCode": "vi",
                                  "title": "Test artifact",
                                  "description": "Artifact description",
                                  "imageUrl": null,
                                  "videoUrl": null,
                                  "audioUrl": null
                                }
                                """.formatted(siteId)))
                .andExpect(status().isCreated())
                .andReturn();
        long artifactId = data(artifactResult).path("id").asLong();

        String beaconUuid = "d9428888-122b-4a2b-8498-bc5b71e0893e";
        MvcResult beaconResult = mockMvc.perform(post("/api/admin/beacons")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "beaconUuid": "%s",
                                  "major": 10,
                                  "minor": 20,
                                  "artifactId": %d,
                                  "placement": "Main gallery",
                                  "latitude": 21.0286,
                                  "longitude": 105.8543,
                                  "txPower": -59,
                                  "active": true
                                }
                                """.formatted(beaconUuid, artifactId)))
                .andExpect(status().isCreated())
                .andReturn();
        long beaconId = data(beaconResult).path("id").asLong();

        mockMvc.perform(get("/api/beacons/resolve")
                        .param("uuid", beaconUuid)
                        .param("major", "10")
                        .param("minor", "20")
                        .param("lang", "vi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.artifact.content.title").value("Test artifact"));

        String eventKey = UUID.randomUUID().toString();
        String event = """
                {
                  "eventKey": "%s",
                  "artifactId": %d,
                  "beaconId": %d,
                  "languageCode": "vi",
                  "durationSeconds": 14
                }
                """.formatted(eventKey, artifactId, beaconId);
        mockMvc.perform(post("/api/interactions")
                        .header("Authorization", bearer(visitorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(event))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duplicate").value(false));
        mockMvc.perform(post("/api/interactions")
                        .header("Authorization", bearer(visitorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(event))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duplicate").value(true));

        mockMvc.perform(post("/api/feedback")
                        .header("Authorization", bearer(visitorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "artifactId": %d,
                                  "rating": 5,
                                  "comment": "Excellent"
                                }
                                """.formatted(artifactId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        mockMvc.perform(get("/api/analytics/dashboard?period=daily")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.interactionsInPeriod").value(1));
    }

    private String registerVisitor(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "email": "%s@example.test",
                                  "password": "StrongPassword123",
                                  "languageCode": "vi"
                                }
                                """.formatted(username, username)))
                .andExpect(status().isCreated())
                .andReturn();
        return data(result).path("accessToken").asText();
    }

    private String login(String identifier, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifier": "%s", "password": "%s"}
                                """.formatted(identifier, password)))
                .andExpect(status().isOk())
                .andReturn();
        return data(result).path("accessToken").asText();
    }

    private JsonNode data(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
