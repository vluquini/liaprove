package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.dtos.AuthenticationRequest;
import com.lia.liaprove.infrastructure.dtos.ChangePasswordRequest;
import com.lia.liaprove.infrastructure.dtos.CreateUserRequest;
import com.lia.liaprove.infrastructure.dtos.UpdateUserRequest;
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

    private String registerAndLogin(String email, String password) throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail(email);
        createUserRequest.setPassword(password);
        createUserRequest.setOccupation("Software Engineer");
        createUserRequest.setExperienceLevel(ExperienceLevel.JUNIOR);
        createUserRequest.setRole(UserRole.PROFESSIONAL);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk());

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return new ObjectMapper().readTree(response).get("token").asText();
    }


    @Test
    @DisplayName("Should update user profile successfully")
    void shouldUpdateUserProfileSuccessfully() throws Exception {
        String token = registerAndLogin("update-test@example.com", "password123");
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

        String token = registerAndLogin(email, oldPassword);
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

        String token = registerAndLogin(email, correctPassword);
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
}
