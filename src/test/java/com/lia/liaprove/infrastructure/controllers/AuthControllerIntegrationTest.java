package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.dtos.user.AuthenticationRequest;
import com.lia.liaprove.infrastructure.dtos.user.CreateUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully register and login a user")
    void shouldRegisterAndLoginUser() throws Exception {
        // Register
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setPassword("password");
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
                "password"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Should return Bad Request when registering with an existing email")
    void shouldReturnBadRequestWhenRegisteringWithExistingEmail() throws Exception {
        // First registration
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setPassword("password");
        createUserRequest.setOccupation("Software Engineer");
        createUserRequest.setExperienceLevel(ExperienceLevel.JUNIOR);
        createUserRequest.setRole(UserRole.PROFESSIONAL);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk());

        // Second registration with the same email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email already registered: test@example.com"));
    }

    @Test
    @DisplayName("Should return Bad Request when registering with an invalid email format")
    void shouldReturnBadRequestWhenRegisteringWithInvalidEmail() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail("invalid-email");
        createUserRequest.setPassword("password");
        createUserRequest.setOccupation("Software Engineer");
        createUserRequest.setExperienceLevel(ExperienceLevel.JUNIOR);
        createUserRequest.setRole(UserRole.PROFESSIONAL);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.email").value("must be a well-formed email address"));
    }

    @Test
    @DisplayName("Should return Unauthorized when logging in with an invalid password")
    void shouldReturnForbiddenWhenLoginWithInvalidPassword() throws Exception {
        // Register user first
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setPassword("password");
        createUserRequest.setOccupation("Software Engineer");
        createUserRequest.setExperienceLevel(ExperienceLevel.JUNIOR);
        createUserRequest.setRole(UserRole.PROFESSIONAL);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk());

        // Attempt login with wrong password
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                "test@example.com",
                "wrongpassword"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized: Bad credentials"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    @DisplayName("Should return Unauthorized when logging in with a non-existent user")
    void shouldReturnForbiddenWhenLoginWithNonExistentUser() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                "nonexistent@example.com",
                "password"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized: Bad credentials"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
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
                .andExpect(jsonPath("$.error.password").value("size must be between 6 and 2147483647"))
                .andExpect(jsonPath("$.error.role").value("must not be null"));
    }
}

