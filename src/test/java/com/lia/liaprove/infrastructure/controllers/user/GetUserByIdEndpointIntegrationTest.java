package com.lia.liaprove.infrastructure.controllers.user;

import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = "classpath:db/h2-populate-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GetUserByIdEndpointIntegrationTest {

    private static final String DEV_USER_HEADER = "X-Dev-User-Email";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return own full profile via GET /me (includes email)")
    void shouldReturnOwnFullProfileViaMe() throws Exception {
        UserEntity user = getSeededUser("mariana.costa@example.com");

        mockMvc.perform(get("/api/v1/users/me")
                        .header(DEV_USER_HEADER, user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.name").value("Mariana Costa"))
                .andExpect(jsonPath("$.email").value("mariana.costa@example.com"))
                .andExpect(jsonPath("$.hardSkills.length()").value(2))
                .andExpect(jsonPath("$.hardSkills[0]").value("python"))
                .andExpect(jsonPath("$.softSkills.length()").value(2))
                .andExpect(jsonPath("$.softSkills[0]").value("analytical thinking"));
    }

    @Test
    @DisplayName("Should return public profile by id (no email) when requester is authenticated")
    void shouldReturnPublicProfileByIdWithoutEmail() throws Exception {
        UserEntity targetUser = getSeededUser("carlos.silva@example.com");

        mockMvc.perform(get("/api/v1/users/{id}", targetUser.getId())
                        .header(DEV_USER_HEADER, "mariana.costa@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(targetUser.getId().toString()))
                .andExpect(jsonPath("$.name").value("Carlos Silva"))
                // email must NOT be present in the public profile to prevent PII leakage
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    @DisplayName("Should return own public profile by id (no email)")
    void shouldReturnOwnPublicProfileByIdWithoutEmail() throws Exception {
        UserEntity user = getSeededUser("mariana.costa@example.com");

        mockMvc.perform(get("/api/v1/users/{id}", user.getId())
                        .header(DEV_USER_HEADER, user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.name").value("Mariana Costa"))
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    @DisplayName("Should return unauthorized when header is missing on /me")
    void shouldReturnUnauthorizedWhenHeaderIsMissingOnMe() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should return unauthorized when header is missing on /{id}")
    void shouldReturnUnauthorizedWhenHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should return not found when user id does not exist")
    void shouldReturnNotFoundWhenUserIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", UUID.fromString("11111111-1111-1111-1111-111111111111"))
                        .header(DEV_USER_HEADER, "mariana.costa@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private UserEntity getSeededUser(String email) {
        return userJpaRepository.findByEmail(email).orElseThrow();
    }
}
