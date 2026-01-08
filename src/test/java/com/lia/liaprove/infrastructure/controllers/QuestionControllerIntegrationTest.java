package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.dtos.AuthenticationRequest;
import com.lia.liaprove.infrastructure.dtos.CreateUserRequest;
import com.lia.liaprove.infrastructure.dtos.question.UpdateQuestionRequest;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class QuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @AfterEach
    void tearDown() {
        questionJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    private String registerAndLogin(String email, String password, UserRole role) throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test " + role.name());
        createUserRequest.setEmail(email);
        createUserRequest.setPassword(password);
        createUserRequest.setOccupation("Tester");
        createUserRequest.setExperienceLevel(ExperienceLevel.SENIOR);
        createUserRequest.setRole(role);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk());

        AuthenticationRequest authRequest = new AuthenticationRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return new ObjectMapper().readTree(response).get("token").asText();
    }

    private QuestionEntity createTestQuestion() {
        MultipleChoiceQuestion questionDomain = new MultipleChoiceQuestion(
                List.of(
                        new Alternative(null, "Alternative A", false),
                        new Alternative(null, "Alternative B", true)
                )
        );
        questionDomain.setAuthorId(UUID.randomUUID());
        questionDomain.setTitle("Original Title");
        questionDomain.setDescription("Original description.");
        questionDomain.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        questionDomain.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        questionDomain.setStatus(QuestionStatus.APPROVED);
        questionDomain.setSubmissionDate(LocalDateTime.now());
        questionDomain.setVotingEndDate(LocalDateTime.now().plusDays(7));
        questionDomain.setRelevanceByCommunity(RelevanceLevel.THREE);
        questionDomain.setRelevanceByLLM(RelevanceLevel.FOUR);
        questionDomain.setRecruiterUsageCount(0);


        QuestionEntity entity = questionMapper.toEntity(questionDomain);
        return questionJpaRepository.save(entity);
    }

    @Test
    @DisplayName("Should update question successfully when user is ADMIN")
    void shouldUpdateQuestionSuccessfully_WhenUserIsAdmin() throws Exception {
        // Setup
        String adminToken = registerAndLogin("admin.update@example.com", "password123", UserRole.ADMIN);
        QuestionEntity question = createTestQuestion();
        UUID questionId = question.getId();

        List<Alternative> newAlternatives = List.of(
                new Alternative(null, "Updated Alt X", true),
                new Alternative(null, "Updated Alt Y", false)
        );
        UpdateQuestionRequest updateRequest = new UpdateQuestionRequest(
                "Updated Title",
                "Updated description.",
                Set.of(KnowledgeArea.DATABASE, KnowledgeArea.AI),
                newAlternatives
        );

        // Act & Assert
        mockMvc.perform(put("/api/v1/questions/{questionId}", questionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(questionId.toString())))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.description", is("Updated description.")))
                .andExpect(jsonPath("$.knowledgeAreas", hasSize(2)))
                .andExpect(jsonPath("$.knowledgeAreas[?(@ == 'DATABASE')]").exists())
                .andExpect(jsonPath("$.knowledgeAreas[?(@ == 'AI')]").exists())
                .andExpect(jsonPath("$.alternatives", hasSize(2)))
                .andExpect(jsonPath("$.alternatives[0].text", is("Updated Alt X")));
    }

    @Test
    @DisplayName("Should return Forbidden when user is not ADMIN")
    void shouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        // Setup
        String professionalToken = registerAndLogin("professional.update@example.com", "password123", UserRole.PROFESSIONAL);
        QuestionEntity question = createTestQuestion();
        UUID questionId = question.getId();

        UpdateQuestionRequest updateRequest = new UpdateQuestionRequest(
                "Attempted Update", null, null, null
        );

        // Act & Assert
        mockMvc.perform(put("/api/v1/questions/{questionId}", questionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + professionalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return Not Found when question does not exist")
    void shouldReturnNotFound_WhenQuestionDoesNotExist() throws Exception {
        // Setup
        String adminToken = registerAndLogin("admin.notfound@example.com", "password123", UserRole.ADMIN);
        UUID nonExistentId = UUID.randomUUID();

        UpdateQuestionRequest updateRequest = new UpdateQuestionRequest(
                "Title for non-existent question", null, null, null
        );

        // Act & Assert
        mockMvc.perform(put("/api/v1/questions/{questionId}", nonExistentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }
}
