package com.lia.liaprove.infrastructure.controllers.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.dtos.user.CreateUserRequest;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.entities.user.UserProfessionalEntity;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthRegisterEndpointIntegrationTest {

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
    @DisplayName("Should successfully register a professional user and return token")
    void shouldRegisterProfessionalUserSuccessfully() throws Exception {
        CreateUserRequest request = createProfessionalRequest();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Should persist professional hard and soft skills on registration")
    void shouldPersistProfessionalSkillsOnRegistration() throws Exception {
        CreateUserRequest request = createProfessionalRequest();
        request.setName("Skilled User");
        request.setEmail("skills@example.com");
        request.setOccupation("Backend Engineer");
        request.setExperienceLevel(ExperienceLevel.PLENO);
        request.setHardSkills(List.of("Java", "Spring Boot", "Java"));
        request.setSoftSkills(List.of("Communication", "Leadership"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        UserEntity entity = userJpaRepository.findByEmail("skills@example.com").orElseThrow();
        assertThat(entity).isInstanceOf(UserProfessionalEntity.class);

        UserProfessionalEntity professional = (UserProfessionalEntity) entity;
        assertThat(professional.getHardSkills()).containsExactly("Java", "Spring Boot");
        assertThat(professional.getSoftSkills()).containsExactly("Communication", "Leadership");
    }

    @Test
    @DisplayName("Should return Bad Request when registering with an invalid email format")
    void shouldReturnBadRequestWhenRegisteringWithInvalidEmail() throws Exception {
        CreateUserRequest request = createProfessionalRequest();
        request.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.email").value("must be a well-formed email address"));
    }

    @Test
    @DisplayName("Should return Bad Request when recruiter fields are missing")
    void shouldReturnBadRequestWhenRecruiterFieldsAreMissing() throws Exception {
        CreateUserRequest request = createProfessionalRequest();
        request.setRole(UserRole.RECRUITER);
        request.setCompanyName(null);
        request.setCompanyEmail(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.companyName").value("Company name is required for recruiters."))
                .andExpect(jsonPath("$.error.companyEmail").value("Company email is required for recruiters."));
    }

    @Test
    @DisplayName("Should successfully register a recruiter user and return token")
    void shouldRegisterRecruiterUserSuccessfully() throws Exception {
        CreateUserRequest request = createProfessionalRequest();
        request.setName("Recruiter User");
        request.setEmail("recruiter@example.com");
        request.setRole(UserRole.RECRUITER);
        request.setCompanyName("Lia Company");
        request.setCompanyEmail("hr@lia.com");
        request.setOccupation(null);
        request.setExperienceLevel(null);
        request.setHardSkills(null);
        request.setSoftSkills(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        UserEntity entity = userJpaRepository.findByEmail("recruiter@example.com").orElseThrow();
        assertThat(entity).isInstanceOf(UserRecruiterEntity.class);

        UserRecruiterEntity recruiter = (UserRecruiterEntity) entity;
        assertThat(recruiter.getCompanyName()).isEqualTo("Lia Company");
        assertThat(recruiter.getCompanyEmail()).isEqualTo("hr@lia.com");
    }

    @Test
    @DisplayName("Should return Bad Request when email is already registered")
    void shouldReturnBadRequestWhenEmailIsAlreadyRegistered() throws Exception {
        CreateUserRequest request = createProfessionalRequest();
        request.setEmail("duplicate@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Email already registered: duplicate@example.com")));
    }

    @Test
    @DisplayName("Should return Bad Request with validation errors for invalid user data")
    void shouldReturnBadRequestWithValidationErrorsForInvalidUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("");
        request.setEmail("not-an-email");
        request.setPassword("123");
        request.setRole(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.name").value("must not be blank"))
                .andExpect(jsonPath("$.error.email").value("must be a well-formed email address"))
                .andExpect(jsonPath("$.error.password").value("size must be at least 6"))
                .andExpect(jsonPath("$.error.role").value("must not be null"));
    }

    private CreateUserRequest createProfessionalRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setOccupation("Software Engineer");
        request.setExperienceLevel(ExperienceLevel.JUNIOR);
        request.setRole(UserRole.PROFESSIONAL);
        return request;
    }
}
