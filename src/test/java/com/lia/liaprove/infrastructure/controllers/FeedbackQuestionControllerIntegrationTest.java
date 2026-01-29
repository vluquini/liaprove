package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.dtos.AuthenticationRequest;
import com.lia.liaprove.infrastructure.dtos.CreateUserRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.SubmitFeedbackQuestionRequest;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FeedbackQuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private FeedbackQuestionJpaRepository feedbackQuestionJpaRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @AfterEach
    void tearDown() {
        feedbackQuestionJpaRepository.deleteAll();
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
        if (role == UserRole.RECRUITER) {
            createUserRequest.setCompanyName("Test Inc.");
            createUserRequest.setCompanyEmail("recruiter@testinc.com");
        }


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

    private QuestionEntity createTestQuestion(QuestionStatus status) {
        MultipleChoiceQuestion questionDomain = new MultipleChoiceQuestion(
                List.of(
                        new Alternative(null, "Alternative A", false),
                        new Alternative(null, "Alternative B", true)
                )
        );
        questionDomain.setAuthorId(UUID.randomUUID());
        questionDomain.setTitle("Test Question Title - " + UUID.randomUUID());
        questionDomain.setDescription("Test question description. - " + UUID.randomUUID());
        questionDomain.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        questionDomain.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        questionDomain.setStatus(status);
        questionDomain.setSubmissionDate(LocalDateTime.now());
        questionDomain.setVotingEndDate(LocalDateTime.now().plusDays(7));
        questionDomain.setRelevanceByCommunity(RelevanceLevel.THREE);
        questionDomain.setRelevanceByLLM(RelevanceLevel.FOUR);
        questionDomain.setRecruiterUsageCount(0);

        QuestionEntity entity = questionMapper.toEntity(questionDomain);
        return questionJpaRepository.save(entity);
    }

    @Test
    @DisplayName("Should submit feedback successfully when user is authenticated")
    void shouldSubmitFeedbackSuccessfully_WhenUserIsAuthenticated() throws Exception {
        // Arrange
        String professionalToken = registerAndLogin("feedback.user@example.com", "password123", UserRole.PROFESSIONAL);
        QuestionEntity question = createTestQuestion(QuestionStatus.VOTING);

        SubmitFeedbackQuestionRequest feedbackRequest = new SubmitFeedbackQuestionRequest(
                "This is a test feedback comment.",
                DifficultyLevel.EASY,
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                RelevanceLevel.FIVE
        );

        // Act
        mockMvc.perform(post("/api/v1/questions/{questionId}/feedback", question.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + professionalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackRequest)))
                .andExpect(status().isCreated());

        // Assert
        List<FeedbackQuestionEntity> feedbacks = feedbackQuestionJpaRepository.findByQuestionId(question.getId());
        assertThat(feedbacks).hasSize(1);
        FeedbackQuestionEntity savedFeedback = feedbacks.get(0);
        assertThat(savedFeedback.getComment()).isEqualTo("This is a test feedback comment.");
        assertThat(savedFeedback.getDifficultyLevel()).isEqualTo(DifficultyLevel.EASY);
        assertThat(savedFeedback.getKnowledgeArea()).isEqualTo(KnowledgeArea.SOFTWARE_DEVELOPMENT);
        assertThat(savedFeedback.getRelevanceLevel()).isEqualTo(RelevanceLevel.FIVE);
        assertThat(savedFeedback.getQuestion().getId()).isEqualTo(question.getId());
        assertThat(savedFeedback.getUser()).isNotNull();
    }

    @Test
    @DisplayName("Should return Unauthorized when submitting feedback without authentication")
    void shouldReturnUnauthorizedWhenSubmittingFeedbackWithoutAuth() throws Exception {
        // Arrange
        QuestionEntity question = createTestQuestion(QuestionStatus.VOTING);

        SubmitFeedbackQuestionRequest feedbackRequest = new SubmitFeedbackQuestionRequest(
                "This is a test feedback comment.",
                DifficultyLevel.EASY,
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                RelevanceLevel.FIVE
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/questions/{questionId}/feedback", question.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return Bad Request when submitting feedback with invalid data")
    void shouldReturnBadRequestForInvalidData() throws Exception {
        // Arrange
        String professionalToken = registerAndLogin("feedback.user.badrequest@example.com", "password123", UserRole.PROFESSIONAL);
        QuestionEntity question = createTestQuestion(QuestionStatus.VOTING);

        SubmitFeedbackQuestionRequest invalidFeedbackRequest = new SubmitFeedbackQuestionRequest(
                "", // Blank comment
                null, // Null difficulty
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                RelevanceLevel.FIVE
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/questions/{questionId}/feedback", question.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + professionalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFeedbackRequest)))
                .andExpect(status().isBadRequest());
    }
}
