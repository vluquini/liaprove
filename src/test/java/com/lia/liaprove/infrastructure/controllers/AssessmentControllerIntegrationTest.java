package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.infrastructure.dtos.assessment.StartSystemAssessmentRequest;
import com.lia.liaprove.infrastructure.dtos.assessment.SubmitAssessmentRequest;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
import com.lia.liaprove.infrastructure.entities.users.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {
        "classpath:db/h2-populate-users.sql",
        "classpath:db/h2-populate-questions.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AssessmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;

    private static final UUID RECRUITER_ID = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15");

    @AfterEach
    void tearDown() {
        assessmentAttemptJpaRepository.deleteAll();
        assessmentJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    private UserEntity getSeededUserEntity(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seeded user not found: " + email));
    }

    @Test
    @DisplayName("Should start system assessment successfully")
    void shouldStartSystemAssessmentSuccessfully() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        StartSystemAssessmentRequest request = new StartSystemAssessmentRequest(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM,
                SystemAssessmentType.MULTIPLE_CHOICE
        );

        mockMvc.perform(post("/api/v1/assessments/start-system")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.userId").value(user.getId().toString()));
    }

    @Test
    @DisplayName("Should return 400 when starting system assessment with null knowledge area")
    void shouldReturnBadRequestWhenStartingWithNullKnowledgeArea() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        StartSystemAssessmentRequest request = new StartSystemAssessmentRequest(
                null,
                DifficultyLevel.MEDIUM,
                SystemAssessmentType.MULTIPLE_CHOICE
        );

        mockMvc.perform(post("/api/v1/assessments/start-system")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 401 when starting system assessment without authentication")
    void shouldReturnUnauthorizedWhenStartingSystemAssessmentWithoutAuth() throws Exception {
        StartSystemAssessmentRequest request = new StartSystemAssessmentRequest(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM,
                SystemAssessmentType.MULTIPLE_CHOICE
        );

        mockMvc.perform(post("/api/v1/assessments/start-system")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 404 when starting personalized assessment with invalid token")
    void shouldReturnNotFoundWhenStartingWithInvalidToken() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        mockMvc.perform(post("/api/v1/assessments/start-personalized/invalid-token-123")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 401 when starting personalized assessment without authentication")
    void shouldReturnUnauthorizedWhenStartingPersonalizedAssessmentWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/v1/assessments/start-personalized/some-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 404 when submitting assessment with non-existent attempt ID")
    void shouldReturnNotFoundWhenSubmittingNonExistentAttempt() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        UUID nonExistentAttemptId = UUID.randomUUID();

        SubmitAssessmentRequest request = new SubmitAssessmentRequest(
                List.of(new SubmitAssessmentRequest.QuestionAnswerRequest(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null
                ))
        );

        mockMvc.perform(post("/api/v1/assessments/" + nonExistentAttemptId + "/submit")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 401 when submitting assessment without authentication")
    void shouldReturnUnauthorizedWhenSubmittingWithoutAuth() throws Exception {
        SubmitAssessmentRequest request = new SubmitAssessmentRequest(
                List.of(new SubmitAssessmentRequest.QuestionAnswerRequest(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null
                ))
        );

        mockMvc.perform(post("/api/v1/assessments/" + UUID.randomUUID() + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 400 when submitting assessment with empty answers")
    void shouldReturnBadRequestWhenSubmittingWithEmptyAnswers() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        SubmitAssessmentRequest request = new SubmitAssessmentRequest(List.of());

        mockMvc.perform(post("/api/v1/assessments/" + UUID.randomUUID() + "/submit")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==========================

    @Test
    @DisplayName("Should start personalized assessment successfully (real flow)")
    void shouldStartPersonalizedAssessmentSuccessfully() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        UserRecruiterEntity recruiter = (UserRecruiterEntity) userJpaRepository.findById(RECRUITER_ID).orElseThrow();
        QuestionEntity question = questionJpaRepository.findById(
                UUID.fromString("00000001-0000-0000-0000-000000000001")
        ).orElseThrow();

        PersonalizedAssessmentEntity assessment = new PersonalizedAssessmentEntity();
        assessment.setTitle("Tech Test 2026");
        assessment.setDescription("Test for Java Developers");
        assessment.setCreationDate(LocalDateTime.now());
        assessment.setCreatedBy(recruiter);
        assessment.setShareableToken("token-xyz-789");
        assessment.setStatus(PersonalizedAssessmentStatus.ACTIVE);
        assessment.setMaxAttempts(2);
        assessment.setQuestions(List.of(question));

        assessmentJpaRepository.save(assessment);

        mockMvc.perform(post("/api/v1/assessments/start-personalized/token-xyz-789")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Should complete full flow: start system assessment and submit")
    void shouldCompleteFullFlowSuccessfully() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        // 1. Start
        StartSystemAssessmentRequest startRequest = new StartSystemAssessmentRequest(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM,
                SystemAssessmentType.MULTIPLE_CHOICE
        );

        MvcResult result = mockMvc.perform(post("/api/v1/assessments/start-system")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String attemptId = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        // 2. Submit
        UUID questionId = UUID.fromString("00000001-0000-0000-0000-000000000001");
        UUID alternativeId = UUID.fromString("00000001-a003-0000-0000-000000000001");

        SubmitAssessmentRequest submitRequest = new SubmitAssessmentRequest(
                List.of(new SubmitAssessmentRequest.QuestionAnswerRequest(
                        questionId,
                        alternativeId,
                        null
                ))
        );

        mockMvc.perform(post("/api/v1/assessments/" + attemptId + "/submit")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.score").exists());
    }
}
