package com.lia.liaprove.infrastructure.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.infrastructure.dtos.user.ChangePasswordRequest;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = "classpath:db/h2-populate-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ChangeUserPasswordEndpointIntegrationTest {

    private static final String DEV_USER_HEADER = "X-Dev-User-Email";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should change password successfully")
    void shouldChangePasswordSuccessfully() throws Exception {
        UserEntity user = getSeededUser("mariana.costa@example.com");
        String previousHash = user.getPasswordHash();

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("password");
        request.setNewPassword("newSecret456");

        mockMvc.perform(patch("/api/v1/users/{id}/password", user.getId())
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        UserEntity updated = userJpaRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getPasswordHash()).isNotEqualTo(previousHash);
        assertThat(passwordEncoder.matches("newSecret456", updated.getPasswordHash())).isTrue();
    }

    @Test
    @DisplayName("Should reject password change when old password is invalid")
    void shouldRejectPasswordChangeWhenOldPasswordIsInvalid() throws Exception {
        UserEntity user = getSeededUser("mariana.costa@example.com");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong-password");
        request.setNewPassword("newSecret456");

        mockMvc.perform(patch("/api/v1/users/{id}/password", user.getId())
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized: Invalid old password."));
    }

    @Test
    @DisplayName("Should reject password change when new password is too short")
    void shouldRejectPasswordChangeWhenNewPasswordIsTooShort() throws Exception {
        UserEntity user = getSeededUser("mariana.costa@example.com");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("password");
        request.setNewPassword("123");

        mockMvc.perform(patch("/api/v1/users/{id}/password", user.getId())
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error.newPassword").value("Password must be at least 6 characters long"));
    }

    @Test
    @DisplayName("Should return unauthorized when changing password without authentication")
    void shouldReturnUnauthorizedWhenChangingPasswordWithoutAuthentication() throws Exception {
        UserEntity user = getSeededUser("mariana.costa@example.com");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("password");
        request.setNewPassword("newSecret456");

        mockMvc.perform(patch("/api/v1/users/{id}/password", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    private UserEntity getSeededUser(String email) {
        return userJpaRepository.findByEmail(email).orElseThrow();
    }
}
