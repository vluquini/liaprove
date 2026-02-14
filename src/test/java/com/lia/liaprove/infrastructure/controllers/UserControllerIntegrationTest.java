package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.dtos.user.AuthenticationRequest;
import com.lia.liaprove.infrastructure.dtos.user.ChangePasswordRequest;
import com.lia.liaprove.infrastructure.dtos.user.CreateUserRequest;
import com.lia.liaprove.infrastructure.dtos.user.UpdateUserRequest;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private String registerAndLogin(String email, String password, UserRole role) throws Exception {
        registerUser(email, password, role);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return new ObjectMapper().readTree(response).get("token").asText();
    }

    private UUID registerUser(String email, String password, UserRole role) throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test " + role.name());
        createUserRequest.setEmail(email);
        createUserRequest.setPassword(password);
        createUserRequest.setOccupation("Software Engineer");
        createUserRequest.setExperienceLevel(ExperienceLevel.JUNIOR);
        createUserRequest.setRole(role);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk());
        return userJpaRepository.findByEmail(email).get().getId();
    }


    @Test
    @DisplayName("Should update user profile successfully")
    void shouldUpdateUserProfileSuccessfully() throws Exception {
        String token = registerAndLogin("update-test@example.com", "password123", UserRole.PROFESSIONAL);
        UUID userId = userJpaRepository.findByEmail("update-test@example.com").get().getId();

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Updated Name");
        updateUserRequest.setEmail("updated-email@example.com");
        updateUserRequest.setOccupation("Senior Software Engineer");
        updateUserRequest.setBio("An updated bio.");
        updateUserRequest.setExperienceLevel(ExperienceLevel.SENIOR);


        mockMvc.perform(put("/users/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated-email@example.com"))
                .andExpect(jsonPath("$.occupation").value("Senior Software Engineer"))
                .andExpect(jsonPath("$.bio").value("An updated bio."))
                .andExpect(jsonPath("$.experienceLevel").value("SENIOR"));
    }

    @Test
    @DisplayName("Should change user password successfully")
    void shouldChangeUserPasswordSuccessfully() throws Exception {
        String email = "changepass-test@example.com";
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword456";

        String token = registerAndLogin(email, oldPassword, UserRole.PROFESSIONAL);
        UUID userId = userJpaRepository.findByEmail(email).get().getId();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(oldPassword);
        changePasswordRequest.setNewPassword(newPassword);

        mockMvc.perform(patch("/users/" + userId + "/password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isNoContent());

        // Verify login with old password fails
        AuthenticationRequest oldAuthRequest = new AuthenticationRequest(email, oldPassword);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oldAuthRequest)))
                .andExpect(status().isUnauthorized());

        // Verify login with new password succeeds
        AuthenticationRequest newAuthRequest = new AuthenticationRequest(email, newPassword);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Should return Unauthorized when changing password with incorrect old password")
    void shouldReturnUnauthorizedWhenChangingPasswordWithIncorrectOldPassword() throws Exception {
        String email = "changepass-fail@example.com";
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String newPassword = "newPassword";

        String token = registerAndLogin(email, correctPassword, UserRole.PROFESSIONAL);
        UUID userId = userJpaRepository.findByEmail(email).get().getId();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(wrongPassword);
        changePasswordRequest.setNewPassword(newPassword);

        mockMvc.perform(patch("/users/" + userId + "/password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized: Invalid old password."))
                .andExpect(jsonPath("$.path").value("/users/" + userId + "/password"));
    }

    @Test
    @DisplayName("Should delete user successfully when user deletes self")
    void shouldDeleteUserSuccessfully_WhenUserDeletesSelf() throws Exception {
        String email = "delete-self@example.com";
        String password = "password123";
        String token = registerAndLogin(email, password, UserRole.PROFESSIONAL);
        UUID userId = userJpaRepository.findByEmail(email).get().getId();

        mockMvc.perform(delete("/users/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        AuthenticationRequest authRequest = new AuthenticationRequest(email, password);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should delete user successfully when admin deletes user")
    void shouldDeleteUserSuccessfully_WhenAdminDeletesUser() throws Exception {
        String adminEmail = "admin-delete@example.com";
        String adminPassword = "adminPassword";
        String adminToken = registerAndLogin(adminEmail, adminPassword, UserRole.ADMIN);

        String userEmail = "user-to-delete@example.com";
        String userPassword = "userPassword";
        UUID userId = registerUser(userEmail, userPassword, UserRole.PROFESSIONAL);

        mockMvc.perform(delete("/users/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        AuthenticationRequest authRequest = new AuthenticationRequest(userEmail, userPassword);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return Forbidden when user deletes another user")
    void shouldReturnForbidden_WhenUserDeletesAnotherUser() throws Exception {
        String userAEmail = "user-a@example.com";
        UUID userAId = registerUser(userAEmail, "passwordA", UserRole.PROFESSIONAL);

        String userBEmail = "user-b@example.com";
        String userBToken = registerAndLogin(userBEmail, "passwordB", UserRole.PROFESSIONAL);

        mockMvc.perform(delete("/users/" + userAId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userBToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return Unauthorized when unauthenticated user deletes user")
    void shouldReturnUnauthorized_WhenUnauthenticatedUserDeletesUser() throws Exception {
        String userEmail = "unauth-delete@example.com";
        UUID userId = registerUser(userEmail, "password123", UserRole.PROFESSIONAL);

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return Not Found when deleting non-existent user")
    void shouldReturnNotFound_WhenDeletingNonExistentUser() throws Exception {
        String adminToken = registerAndLogin("admin-nonexistent@example.com", "password", UserRole.ADMIN);
        UUID nonExistentUserId = UUID.randomUUID();

        mockMvc.perform(delete("/users/" + nonExistentUserId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return Forbidden when admin deletes self")
    void shouldReturnForbidden_WhenAdminDeletesSelf() throws Exception {
        String adminEmail = "admin-selfdelete@example.com";
        String adminPassword = "password";
        String adminToken = registerAndLogin(adminEmail, adminPassword, UserRole.ADMIN);
        UUID adminId = userJpaRepository.findByEmail(adminEmail).get().getId();

        mockMvc.perform(delete("/users/" + adminId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return Forbidden when admin deletes another admin")
    void shouldReturnForbidden_WhenAdminDeletesAnotherAdmin() throws Exception {
        String admin1Email = "admin1@example.com";
        String admin1Token = registerAndLogin(admin1Email, "password", UserRole.ADMIN);

        String admin2Email = "admin2@example.com";
        UUID admin2Id = registerUser(admin2Email, "password", UserRole.ADMIN);

        mockMvc.perform(delete("/users/" + admin2Id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin1Token))
                .andExpect(status().isForbidden());
    }
}
