package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:db/h2-populate-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AdminUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    @DisplayName("Should list users with filters for admin")
    void shouldListUsersForAdmin() throws Exception {
        UserEntity admin = getSeededUser("admin@liaprove.com");

        mockMvc.perform(get("/api/v1/admin/users")
                        .header("X-Dev-User-Email", admin.getEmail())
                        .param("role", "PROFESSIONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))); // Carlos, Mariana e Junior
    }

    @Test
    @DisplayName("Should hard delete user successfully as admin")
    void shouldHardDeleteUserAsAdmin() throws Exception {
        UserEntity admin = getSeededUser("admin@liaprove.com");
        UserEntity target = getSeededUser("mariana.costa@example.com");

        mockMvc.perform(delete("/api/v1/admin/users/{id}", target.getId())
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isNoContent());

        assertThat(userJpaRepository.existsById(target.getId())).isFalse();
    }

    @Test
    @DisplayName("Should deactivate user successfully as admin")
    void shouldDeactivateUserAsAdmin() throws Exception {
        UserEntity admin = getSeededUser("admin@liaprove.com");
        UserEntity target = getSeededUser("carlos.silva@example.com");

        mockMvc.perform(patch("/api/v1/admin/users/{id}/deactivate", target.getId())
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isOk());

        UserEntity updated = userJpaRepository.findById(target.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("Should activate user successfully as admin")
    void shouldActivateUserAsAdmin() throws Exception {
        UserEntity admin = getSeededUser("admin@liaprove.com");
        UserEntity target = getSeededUser("carlos.silva@example.com");
        target.setStatus(UserStatus.INACTIVE);
        userJpaRepository.save(target);

        mockMvc.perform(patch("/api/v1/admin/users/{id}/activate", target.getId())
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isOk());

        UserEntity updated = userJpaRepository.findById(target.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return forbidden when non-admin accesses admin endpoints")
    void shouldReturnForbiddenForNonAdmin() throws Exception {
        UserEntity professional = getSeededUser("carlos.silva@example.com");

        mockMvc.perform(get("/api/v1/admin/users")
                        .header("X-Dev-User-Email", professional.getEmail()))
                .andExpect(status().isForbidden());
    }
}
