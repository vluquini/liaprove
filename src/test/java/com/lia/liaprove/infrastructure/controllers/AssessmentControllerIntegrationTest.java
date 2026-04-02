package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.infrastructure.dtos.assessment.*;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .andExpect(jsonPath("$.attemptId").exists());
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
    @DisplayName("Should submit assessment with empty answers and return FAILED")
    void shouldReturnFailedWhenSubmittingWithEmptyAnswers() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        // Criar attempt real primeiro (igual ao fluxo completo)
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
        String attemptId = JsonPath.read(response, "$.attemptId");

        // 2. Submit com lista vazia - deve retornar FAILED (0% accuracy)
        SubmitAssessmentRequest request = new SubmitAssessmentRequest(List.of());

        mockMvc.perform(post("/api/v1/assessments/" + attemptId + "/submit")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.accuracyRate").value(0));
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
        assessment.setEvaluationTimerSeconds(1800L); // 30 minutes
        assessment.setQuestions(List.of(question));

        assessmentJpaRepository.save(assessment);

        mockMvc.perform(post("/api/v1/assessments/start-personalized/token-xyz-789")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attemptId").exists());
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

        String attemptId = JsonPath.read(response, "$.attemptId");
        String questionId = JsonPath.read(response, "$.questions[0].id");
        String alternativeId = JsonPath.read(response, "$.questions[0].alternatives[0].id");

        // 2. Submit
        SubmitAssessmentRequest submitRequest = new SubmitAssessmentRequest(
                List.of(new SubmitAssessmentRequest.QuestionAnswerRequest(
                        UUID.fromString(questionId),
                        UUID.fromString(alternativeId),
                        null
                ))
        );

        mockMvc.perform(post("/api/v1/assessments/" + attemptId + "/submit")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.accuracyRate").exists());
    }

    // ==========================
    // POST /api/v1/assessments/personalized
    // ==========================

    @Test
    @DisplayName("Should create personalized assessment successfully")
    void shouldCreatePersonalizedAssessmentSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");
        UUID questionId = UUID.fromString("00000001-0000-0000-0000-000000000001");

        CreatePersonalizedAssessmentRequest request = new CreatePersonalizedAssessmentRequest(
                "Java Developer Test",
                "Assessment for Java positions",
                List.of(questionId),
                LocalDateTime.now().plusDays(7),
                3,
                60
        );

        mockMvc.perform(post("/api/v1/assessments/personalized")
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should return 403 when candidate tries to create personalized assessment")
    void shouldReturnForbiddenWhenCandidateCreatesAssessment() throws Exception {
        UserEntity candidate = getSeededUserEntity("carlos.silva@example.com");
        UUID questionId = UUID.fromString("00000001-0000-0000-0000-000000000001");

        CreatePersonalizedAssessmentRequest request = new CreatePersonalizedAssessmentRequest(
                "Invalid",
                "Should not work",
                List.of(questionId),
                LocalDateTime.now().plusDays(5),
                1,
                30
        );

        mockMvc.perform(post("/api/v1/assessments/personalized")
                        .header("X-Dev-User-Email", candidate.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 400 when creating assessment without title")
    void shouldReturnBadRequestWhenCreatingAssessmentWithoutTitle() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");

        CreatePersonalizedAssessmentRequest request = new CreatePersonalizedAssessmentRequest(
                null,
                "desc",
                List.of(),
                LocalDateTime.now().plusDays(5),
                1,
                30
        );

        mockMvc.perform(post("/api/v1/assessments/personalized")
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    // ==========================
    // GET /api/v1/assessments/personalized/suggestions
    // ==========================

    @Test
    @DisplayName("Should get suggested questions with filters")
    void shouldGetSuggestedQuestionsWithFilters() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");

        mockMvc.perform(get("/api/v1/assessments/personalized/suggestions")
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .param("knowledgeAreas", "SOFTWARE_DEVELOPMENT")
                        .param("difficultyLevels", "MEDIUM")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Should return 403 when candidate tries to get suggestions")
    void shouldReturnForbiddenWhenCandidateGetsSuggestions() throws Exception {
        UserEntity candidate = getSeededUserEntity("carlos.silva@example.com");

        mockMvc.perform(get("/api/v1/assessments/personalized/suggestions")
                        .header("X-Dev-User-Email", candidate.getEmail()))
                .andExpect(status().isForbidden());
    }


    // ==========================
    // PATCH /api/v1/assessments/personalized/{id}
    // ==========================

    @Test
    @DisplayName("Should update personalized assessment successfully")
    void shouldUpdatePersonalizedAssessmentSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");

        PersonalizedAssessmentEntity assessment = createTestAssessment(recruiter);

        UpdatePersonalizedAssessmentRequest request = new UpdatePersonalizedAssessmentRequest(
                LocalDateTime.now().plusDays(10),
                5,
                PersonalizedAssessmentStatus.DEACTIVATED
        );

        mockMvc.perform(patch("/api/v1/assessments/personalized/" + assessment.getId())
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 403 when updating another recruiter's assessment")
    void shouldReturnForbiddenWhenUpdatingAnotherRecruiterAssessment() throws Exception {
        UserEntity recruiter1 = getSeededUserEntity("ana.p@techrecruit.com");
        UserEntity recruiter2 = getSeededUserEntity("roberto.l@hiredev.com");

        PersonalizedAssessmentEntity assessment = createTestAssessment(recruiter1);

        UpdatePersonalizedAssessmentRequest request = new UpdatePersonalizedAssessmentRequest(
                null, 5, null
        );

        mockMvc.perform(patch("/api/v1/assessments/personalized/" + assessment.getId())
                        .header("X-Dev-User-Email", recruiter2.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }


    // ==========================
    // DELETE /api/v1/assessments/personalized/{id}
    // ==========================

    @Test
    @DisplayName("Should delete personalized assessment successfully")
    void shouldDeletePersonalizedAssessmentSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");

        PersonalizedAssessmentEntity assessment = createTestAssessment(recruiter);

        mockMvc.perform(delete("/api/v1/assessments/personalized/" + assessment.getId())
                        .header("X-Dev-User-Email", recruiter.getEmail()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 403 when deleting another recruiter's assessment")
    void shouldReturnForbiddenWhenDeletingAnotherRecruiterAssessment() throws Exception {
        UserEntity recruiter1 = getSeededUserEntity("ana.p@techrecruit.com");
        UserEntity recruiter2 = getSeededUserEntity("roberto.l@hiredev.com");

        PersonalizedAssessmentEntity assessment = createTestAssessment(recruiter1);

        mockMvc.perform(delete("/api/v1/assessments/personalized/" + assessment.getId())
                        .header("X-Dev-User-Email", recruiter2.getEmail()))
                .andExpect(status().isForbidden());
    }


    // ==========================
    // GET /api/v1/assessments/personalized/{id}/attempts
    // ==========================

    @Test
    @DisplayName("Should list attempts for assessment")
    void shouldListAttemptsSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");
        UserEntity candidate = getSeededUserEntity("carlos.silva@example.com");

        PersonalizedAssessmentEntity assessment = createTestAssessment(recruiter);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(assessment);
        attempt.setUser(candidate);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        assessmentAttemptJpaRepository.save(attempt);

        mockMvc.perform(get("/api/v1/assessments/personalized/" + assessment.getId() + "/attempts")
                        .header("X-Dev-User-Email", recruiter.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


    // ==========================
    // GET /api/v1/assessments/attempts/{attemptId}
    // ==========================

    @Test
    @DisplayName("Should get attempt details successfully")
    void shouldGetAttemptDetailsSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");
        UserEntity candidate = getSeededUserEntity("carlos.silva@example.com");

        PersonalizedAssessmentEntity assessment = createTestAssessment(recruiter);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(assessment);
        attempt.setUser(candidate);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        attempt = assessmentAttemptJpaRepository.save(attempt);

        mockMvc.perform(get("/api/v1/assessments/attempts/" + attempt.getId())
                        .header("X-Dev-User-Email", recruiter.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attemptId").value(attempt.getId().toString()));
    }


    // ==========================
    // POST /api/v1/assessments/{attemptId}/evaluate
    // ==========================

    @Test
    @DisplayName("Should evaluate attempt successfully")
    void shouldEvaluateAttemptSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");
        UserEntity candidate = getSeededUserEntity("carlos.silva@example.com");

        PersonalizedAssessmentEntity assessment = createTestAssessment(recruiter);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(assessment);
        attempt.setUser(candidate);
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        attempt.setStartedAt(LocalDateTime.now());
        attempt = assessmentAttemptJpaRepository.save(attempt);

        EvaluateAssessmentAttemptRequest request = new EvaluateAssessmentAttemptRequest(
                AssessmentAttemptStatus.APPROVED
        );

        mockMvc.perform(post("/api/v1/assessments/" + attempt.getId() + "/evaluate")
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }


    // ==========================
    // HELPER
    // ==========================

    private PersonalizedAssessmentEntity createTestAssessment(UserEntity recruiter) {
        PersonalizedAssessmentEntity assessment = new PersonalizedAssessmentEntity();
        assessment.setTitle("Test " + UUID.randomUUID());
        assessment.setDescription("desc");
        assessment.setCreationDate(LocalDateTime.now());
        assessment.setCreatedBy((UserRecruiterEntity) recruiter);
        assessment.setShareableToken("token-" + UUID.randomUUID());
        assessment.setStatus(PersonalizedAssessmentStatus.ACTIVE);
        assessment.setMaxAttempts(3);
        assessment.setEvaluationTimerSeconds(1800L);
        return assessmentJpaRepository.save(assessment);
    }
}
