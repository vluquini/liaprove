package com.lia.liaprove.infrastructure.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.infrastructure.dtos.user.ChangePasswordRequest;
import com.lia.liaprove.infrastructure.dtos.user.UpdateUserRequest;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:db/h2-populate-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserControllerIntegrationTest {

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

    private UserEntity getSeededUser(String email) {
        return userJpaRepository.findByEmail(email).orElseThrow();
    }

    @Test
    @DisplayName("Should update user profile successfully")
    void shouldUpdateUserProfileSuccessfully() throws Exception {
        UserEntity user = getSeededUser("carlos.silva@example.com");

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Carlos Updated");
        updateRequest.setEmail("carlos.new@example.com");
        updateRequest.setOccupation("Senior Developer");
        updateRequest.setExperienceLevel(ExperienceLevel.SENIOR);
        updateRequest.setHardSkills(List.of("Java", "Spring Boot", "PostgreSQL"));
        updateRequest.setSoftSkills(List.of("Leadership", "Communication"));

        mockMvc.perform(put("/api/v1/users/{id}", user.getId())
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos Updated"))
                .andExpect(jsonPath("$.occupation").value("Senior Developer"))
                .andExpect(jsonPath("$.hardSkills[0]").value("Java"))
                .andExpect(jsonPath("$.softSkills[0]").value("Leadership"));
    }

    @Test
    @DisplayName("Should change user password successfully")
    void shouldChangeUserPasswordSuccessfully() throws Exception {
        UserEntity user = getSeededUser("mariana.costa@example.com");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("password"); // Senha padrão do h2-populate-users.sql ("password")
        request.setNewPassword("newSecret456");

        mockMvc.perform(patch("/api/v1/users/{id}/password", user.getId())
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should deactivate own account successfully")
    void shouldDeactivateOwnAccountSuccessfully() throws Exception {
        UserEntity user = getSeededUser("junior.dev@example.com");

        mockMvc.perform(patch("/api/v1/users/me/deactivate")
                        .header("X-Dev-User-Email", user.getEmail()))
                .andExpect(status().isOk());

        UserEntity updated = userJpaRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("Should return unauthorized when accessing without credentials")
    void shouldReturnUnauthorizedWithoutAuth() throws Exception {
        UserEntity user = getSeededUser("carlos.silva@example.com");

        mockMvc.perform(get("/api/v1/users/{id}", user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return professional skills when fetching user by id")
    void shouldReturnProfessionalSkillsWhenFetchingUserById() throws Exception {
        UserEntity user = getSeededUser("mariana.costa@example.com");

        mockMvc.perform(get("/api/v1/users/{id}", user.getId())
                        .header("X-Dev-User-Email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hardSkills[0]").value("Python"))
                .andExpect(jsonPath("$.softSkills[0]").value("Analytical Thinking"));
    }
}
