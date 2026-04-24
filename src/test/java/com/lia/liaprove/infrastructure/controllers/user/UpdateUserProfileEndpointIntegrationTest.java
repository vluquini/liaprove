package com.lia.liaprove.infrastructure.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.infrastructure.dtos.user.UpdateUserRequest;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserProfessionalEntity;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = "classpath:db/h2-populate-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UpdateUserProfileEndpointIntegrationTest {

    private static final String DEV_USER_HEADER = "X-Dev-User-Email";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should update profile and persist professional skills")
    void shouldUpdateProfileAndPersistProfessionalSkills() throws Exception {
        UserEntity user = getSeededUser("carlos.silva@example.com");

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Carlos Updated");
        updateRequest.setEmail("carlos.updated@example.com");
        updateRequest.setOccupation("Senior Developer");
        updateRequest.setBio("Updated bio");
        updateRequest.setExperienceLevel(ExperienceLevel.SENIOR);
        updateRequest.setHardSkills(List.of("Java", "Spring Boot", "PostgreSQL"));
        updateRequest.setSoftSkills(List.of("Leadership", "Communication"));

        mockMvc.perform(put("/api/v1/users/{id}", user.getId())
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.name").value("Carlos Updated"))
                .andExpect(jsonPath("$.email").value("carlos.updated@example.com"))
                .andExpect(jsonPath("$.occupation").value("Senior Developer"))
                .andExpect(jsonPath("$.bio").value("Updated bio"))
                .andExpect(jsonPath("$.hardSkills.length()").value(3))
                .andExpect(jsonPath("$.hardSkills[2]").value("PostgreSQL"))
                .andExpect(jsonPath("$.softSkills.length()").value(2))
                .andExpect(jsonPath("$.softSkills[1]").value("Communication"));

        UserProfessionalEntity updated = (UserProfessionalEntity) userJpaRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Carlos Updated");
        assertThat(updated.getEmail()).isEqualTo("carlos.updated@example.com");
        assertThat(updated.getOccupation()).isEqualTo("Senior Developer");
        assertThat(updated.getBio()).isEqualTo("Updated bio");
        assertThat(updated.getExperienceLevel()).isEqualTo(ExperienceLevel.SENIOR);
        assertThat(updated.getHardSkills()).containsExactly("Java", "Spring Boot", "PostgreSQL");
        assertThat(updated.getSoftSkills()).containsExactly("Leadership", "Communication");
    }

    @Test
    @DisplayName("Should reject update when no field is provided")
    void shouldRejectUpdateWhenNoFieldIsProvided() throws Exception {
        UserEntity user = getSeededUser("carlos.silva@example.com");

        mockMvc.perform(put("/api/v1/users/{id}", user.getId())
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateUserRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("At least one field must be provided to update the profile"));
    }

    @Test
    @DisplayName("Should reject update when email is already used by another user")
    void shouldRejectUpdateWhenEmailIsAlreadyUsedByAnotherUser() throws Exception {
        UserEntity user = getSeededUser("carlos.silva@example.com");

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setEmail("mariana.costa@example.com");

        mockMvc.perform(put("/api/v1/users/{id}", user.getId())
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Email already registered by another user."));
    }

    @Test
    @DisplayName("Should return unauthorized when updating profile without authentication")
    void shouldReturnUnauthorizedWhenUpdatingProfileWithoutAuthentication() throws Exception {
        UserEntity user = getSeededUser("carlos.silva@example.com");

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setOccupation("Senior Developer");

        mockMvc.perform(put("/api/v1/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    private UserEntity getSeededUser(String email) {
        return userJpaRepository.findByEmail(email).orElseThrow();
    }
}
