package com.lia.liaprove.infrastructure.controllers.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.infrastructure.dtos.assessment.StartSystemAssessmentRequest;
import com.lia.liaprove.infrastructure.dtos.assessment.SubmitAssessmentRequest;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.question.OpenQuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.question.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.CANDIDATE_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.createOpenQuestion;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.deleteAssessmentData;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.getSeededRecruiter;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.getSeededUser;
import static org.assertj.core.api.Assertions.assertThat;
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
class SubmitAssessmentEndpointIntegrationTest {

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

    @AfterEach
    void tearDown() {
        deleteAssessmentData(assessmentAttemptJpaRepository, assessmentJpaRepository, userJpaRepository);
    }

    @Test
    @DisplayName("Should return 404 when submitting assessment with non-existent attempt ID")
    void shouldReturnNotFoundWhenSubmittingNonExistentAttempt() throws Exception {
        UserEntity user = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        SubmitAssessmentRequest request = new SubmitAssessmentRequest(
                List.of(new SubmitAssessmentRequest.QuestionAnswerRequest(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null
                ))
        );

        mockMvc.perform(post("/api/v1/assessments/" + UUID.randomUUID() + "/submit")
                        .header(DEV_USER_HEADER, user.getEmail())
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
        UserEntity user = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        String attemptId = startSystemAssessment(user, SystemAssessmentType.MULTIPLE_CHOICE).attemptId();
        SubmitAssessmentRequest request = new SubmitAssessmentRequest(List.of());

        mockMvc.perform(post("/api/v1/assessments/" + attemptId + "/submit")
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.accuracyRate").value(0));
    }

    @Test
    @DisplayName("Should complete full flow: start system assessment and submit")
    void shouldCompleteFullFlowSuccessfully() throws Exception {
        UserEntity user = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        StartedAssessment startedAssessment = startSystemAssessment(user, SystemAssessmentType.MULTIPLE_CHOICE);
        SubmitAssessmentRequest submitRequest = new SubmitAssessmentRequest(
                List.of(new SubmitAssessmentRequest.QuestionAnswerRequest(
                        UUID.fromString(startedAssessment.questionId()),
                        UUID.fromString(startedAssessment.alternativeId()),
                        null
                ))
        );

        mockMvc.perform(post("/api/v1/assessments/" + startedAssessment.attemptId() + "/submit")
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.accuracyRate").exists());
    }

    @Test
    @DisplayName("Should submit system project assessment and keep status as COMPLETED")
    void shouldKeepCompletedStatusWhenSubmittingSystemProjectAssessment() throws Exception {
        UserEntity user = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        StartedAssessment startedAssessment = startSystemAssessment(user, SystemAssessmentType.PROJECT);
        SubmitAssessmentRequest request = new SubmitAssessmentRequest(
                List.of(new SubmitAssessmentRequest.QuestionAnswerRequest(
                        UUID.fromString(startedAssessment.questionId()),
                        null,
                        "https://github.com/acme/project-community-review"
                ))
        );

        mockMvc.perform(post("/api/v1/assessments/" + startedAssessment.attemptId() + "/submit")
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.accuracyRate").value(0));
    }

    @Test
    @Transactional
    @DisplayName("Should propagate text response when submitting personalized assessment with open question")
    void shouldPropagateTextResponseWhenSubmittingPersonalizedAssessmentWithOpenQuestion() throws Exception {
        UserEntity user = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        UserRecruiterEntity recruiter = getSeededRecruiter(userJpaRepository, RECRUITER_EMAIL);
        OpenQuestionEntity openQuestion = createOpenQuestion(
                questionJpaRepository,
                recruiter,
                "Explain the trade-offs of event-driven architecture"
        );

        PersonalizedAssessmentEntity assessment = new PersonalizedAssessmentEntity();
        assessment.setTitle("Open Question Assessment");
        assessment.setDescription("Assessment that includes an open question.");
        assessment.setCreationDate(LocalDateTime.now());
        assessment.setCreatedBy(recruiter);
        assessment.setShareableToken("token-open-question-" + UUID.randomUUID());
        assessment.setStatus(PersonalizedAssessmentStatus.ACTIVE);
        assessment.setMaxAttempts(2);
        assessment.setEvaluationTimerSeconds(1800L);
        assessment.setQuestions(List.of(openQuestion));
        assessment = assessmentJpaRepository.save(assessment);

        MvcResult startResult = mockMvc.perform(post("/api/v1/assessments/start-personalized/" + assessment.getShareableToken())
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String attemptId = JsonPath.read(startResult.getResponse().getContentAsString(), "$.attemptId");
        SubmitAssessmentRequest submitRequest = new SubmitAssessmentRequest(
                List.of(new SubmitAssessmentRequest.QuestionAnswerRequest(
                        openQuestion.getId(),
                        null,
                        null,
                        "The main trade-offs are more operational complexity and weaker synchronous request flows."
                ))
        );

        mockMvc.perform(post("/api/v1/assessments/" + attemptId + "/submit")
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk());

        AssessmentAttemptEntity savedAttempt = assessmentAttemptJpaRepository.findById(UUID.fromString(attemptId))
                .orElseThrow();

        assertThat(savedAttempt.getAnswers()).hasSize(1);
        assertThat(savedAttempt.getAnswers().getFirst().getSelectedAlternativeId()).isNull();
        assertThat(savedAttempt.getAnswers().getFirst().getProjectUrl()).isNull();
        assertThat(savedAttempt.getAnswers().getFirst().getTextResponse())
                .isEqualTo("The main trade-offs are more operational complexity and weaker synchronous request flows.");
    }

    private StartedAssessment startSystemAssessment(UserEntity user, SystemAssessmentType type) throws Exception {
        StartSystemAssessmentRequest startRequest = new StartSystemAssessmentRequest(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM,
                type
        );

        MvcResult result = mockMvc.perform(post("/api/v1/assessments/start-system")
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String attemptId = JsonPath.read(response, "$.attemptId");
        String questionId = JsonPath.read(response, "$.questions[0].id");
        String alternativeId = type == SystemAssessmentType.MULTIPLE_CHOICE
                ? JsonPath.read(response, "$.questions[0].alternatives[0].id")
                : null;

        return new StartedAssessment(attemptId, questionId, alternativeId);
    }

    private record StartedAssessment(String attemptId, String questionId, String alternativeId) {
    }
}
