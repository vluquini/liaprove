package com.lia.liaprove.infrastructure.controllers.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.infrastructure.dtos.user.AuthenticationRequest;
import com.lia.liaprove.infrastructure.dtos.user.CreateUserRequest;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthLoginEndpointIntegrationTest {

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
    @DisplayName("Should successfully login a user and return token")
    void shouldLoginUserSuccessfully() throws Exception {
        registerUser("test.login@example.com", "password123");

        UserEntity beforeLogin = userJpaRepository.findByEmail("test.login@example.com").orElseThrow();
        assertThat(beforeLogin.getStatus()).isEqualTo(UserStatus.ACTIVE);

        AuthenticationRequest request = new AuthenticationRequest("test.login@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        UserEntity afterLogin = userJpaRepository.findByEmail("test.login@example.com").orElseThrow();
        assertThat(afterLogin.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should successfully reactivate an INACTIVE user on login")
    void shouldReactivateUserOnLogin() throws Exception {
        registerUser("inactive@example.com", "password123");

        UserEntity entity = userJpaRepository.findByEmail("inactive@example.com").orElseThrow();
        entity.setStatus(UserStatus.INACTIVE);
        userJpaRepository.save(entity);

        AuthenticationRequest request = new AuthenticationRequest("inactive@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        UserEntity updatedEntity = userJpaRepository.findByEmail("inactive@example.com").orElseThrow();
        assertThat(updatedEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return Unauthorized when logging in with an invalid password")
    void shouldReturnUnauthorizedWhenLoginWithInvalidPassword() throws Exception {
        registerUser("test.login@example.com", "password123");

        AuthenticationRequest request = new AuthenticationRequest("test.login@example.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value(containsString("Unauthorized:")))
                .andExpect(jsonPath("$.error").value(containsString("Bad credentials")));
    }

    @Test
    @DisplayName("Should return Unauthorized when logging in with an unknown email")
    void shouldReturnUnauthorizedWhenLoginWithUnknownEmail() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("missing@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value(containsString("Unauthorized:")));
    }

    private void registerUser(String email, String password) throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Test User");
        request.setEmail(email);
        request.setPassword(password);
        request.setOccupation("Engineer");
        request.setRole(UserRole.PROFESSIONAL);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
