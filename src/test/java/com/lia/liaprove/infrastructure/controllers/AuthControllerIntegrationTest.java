package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.infrastructure.dtos.user.AuthenticationRequest;
import com.lia.liaprove.infrastructure.dtos.user.CreateUserRequest;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

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
    @DisplayName("Should successfully register and login a user")
    void shouldRegisterAndLoginUser() throws Exception {
        // Register
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setPassword("password123");
        createUserRequest.setOccupation("Software Engineer");
        createUserRequest.setExperienceLevel(ExperienceLevel.JUNIOR);
        createUserRequest.setRole(UserRole.PROFESSIONAL);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        // Login
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Should successfully reactivate an INACTIVE user on login")
    void shouldReactivateUserOnLogin() throws Exception {
        // 1. Register user
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Inactive User");
        createUserRequest.setEmail("inactive@example.com");
        createUserRequest.setPassword("password123");
        createUserRequest.setOccupation("Analyst");
        createUserRequest.setRole(UserRole.PROFESSIONAL);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk());

        // 2. Manually set status to INACTIVE in DB
        UserEntity entity = userJpaRepository.findByEmail("inactive@example.com").orElseThrow();
        entity.setStatus(UserStatus.INACTIVE);
        userJpaRepository.save(entity);

        // 3. Attempt login
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                "inactive@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        // 4. Verify status is ACTIVE again
        UserEntity updatedEntity = userJpaRepository.findByEmail("inactive@example.com").orElseThrow();
        assertThat(updatedEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return Bad Request when registering with an invalid email format")
    void shouldReturnBadRequestWhenRegisteringWithInvalidEmail() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail("invalid-email");
        createUserRequest.setPassword("password123");
        createUserRequest.setOccupation("Dev");
        createUserRequest.setRole(UserRole.PROFESSIONAL);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.email").value("must be a well-formed email address"));
    }

    @Test
    @DisplayName("Should return Unauthorized when logging in with an invalid password")
    void shouldReturnUnauthorizedWhenLoginWithInvalidPassword() throws Exception {
        // Register user first
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail("test.login@example.com");
        createUserRequest.setPassword("password123");
        createUserRequest.setOccupation("Engineer");
        createUserRequest.setRole(UserRole.PROFESSIONAL);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk());

        // Attempt login with wrong password
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                "test.login@example.com",
                "wrongpassword"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized: Bad credentials"));
    }

    @Test
    @DisplayName("Should return Bad Request with validation errors for invalid user data")
    void shouldReturnBadRequestWithValidationErrorsForInvalidUser() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("");              // Invalid: blank name
        createUserRequest.setEmail("not-an-email"); // Invalid: bad email format
        createUserRequest.setPassword("123");       // Invalid: too short
        createUserRequest.setRole(null);            // Invalid: null role

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.name").value("must not be blank"))
                .andExpect(jsonPath("$.error.email").value("must be a well-formed email address"))
                .andExpect(jsonPath("$.error.password").value("size must be at least 6"))
                .andExpect(jsonPath("$.error.role").value("must not be null"));
    }
}
