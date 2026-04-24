package com.lia.liaprove.infrastructure.controllers.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.infrastructure.dtos.metrics.SubmitFeedbackOnAssessmentRequest;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptVoteJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.FeedbackAssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.question.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.VoteJpaRepository;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(
        scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class SubmitFeedbackOnAssessmentAttemptEndpointIntegrationTest {

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
    private VoteJpaRepository voteJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;

    @Autowired
    private AssessmentAttemptVoteJpaRepository assessmentAttemptVoteJpaRepository;

    @Autowired
    private FeedbackAssessmentJpaRepository feedbackAssessmentJpaRepository;

    @AfterEach
    void tearDown() {
        MetricsControllerIntegrationTestSupport.deleteMetricsData(
                assessmentAttemptVoteJpaRepository,
                assessmentAttemptJpaRepository,
                voteJpaRepository,
                feedbackAssessmentJpaRepository,
                feedbackQuestionJpaRepository,
                assessmentJpaRepository,
                questionJpaRepository,
                userJpaRepository
        );
    }

    @Test
    @DisplayName("Should submit feedback on assessment attempt successfully")
    void shouldSubmitFeedbackOnAssessmentAttemptSuccessfully() throws Exception {
        UserEntity reviewer = getUser(MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL);
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner, "https://github.com/acme/project-feedback");
        SubmitFeedbackOnAssessmentRequest request = new SubmitFeedbackOnAssessmentRequest(
                "This assessment attempt feedback was created by an integration test."
        );

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/feedback", attempt.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<FeedbackAssessmentEntity> savedFeedbacks = feedbackAssessmentJpaRepository.findAll();
        assertThat(savedFeedbacks).hasSize(1);
        FeedbackAssessmentEntity savedFeedback = savedFeedbacks.getFirst();
        assertThat(savedFeedback.getAssessmentAttemptId()).isEqualTo(attempt.getId());
        assertThat(savedFeedback.getUser().getId()).isEqualTo(reviewer.getId());
        assertThat(savedFeedback.getComment()).isEqualTo("This assessment attempt feedback was created by an integration test.");
        assertThat(savedFeedback.isVisible()).isTrue();
        assertThat(savedFeedback.getSubmissionDate()).isNotNull();
    }

    @Test
    @DisplayName("Should reject duplicate feedback on same assessment attempt")
    void shouldRejectDuplicateFeedbackOnSameAssessmentAttempt() throws Exception {
        UserEntity reviewer = getUser(MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL);
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner, "https://github.com/acme/project-duplicate-feedback");
        SubmitFeedbackOnAssessmentRequest request = new SubmitFeedbackOnAssessmentRequest(
                "Trying to submit feedback twice."
        );

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/feedback", attempt.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/feedback", attempt.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject feedback on own assessment attempt")
    void shouldRejectFeedbackOnOwnAssessmentAttempt() throws Exception {
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner, "https://github.com/acme/project-own-feedback");
        SubmitFeedbackOnAssessmentRequest request = new SubmitFeedbackOnAssessmentRequest(
                "Owner should not be able to comment on own attempt."
        );

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/feedback", attempt.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, owner.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return not found when submitting feedback to non-existent assessment attempt")
    void shouldReturnNotFoundWhenSubmittingFeedbackToNonExistentAssessmentAttempt() throws Exception {
        UserEntity reviewer = getUser(MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL);
        SubmitFeedbackOnAssessmentRequest request = new SubmitFeedbackOnAssessmentRequest(
                "Trying to submit feedback for a missing assessment attempt."
        );

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/feedback", UUID.randomUUID())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return bad request when submitting feedback on assessment attempt with blank comment")
    void shouldReturnBadRequestWhenSubmittingFeedbackOnAssessmentAttemptWithBlankComment() throws Exception {
        UserEntity reviewer = getUser(MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL);
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner, "https://github.com/acme/project-blank-feedback");
        SubmitFeedbackOnAssessmentRequest request = new SubmitFeedbackOnAssessmentRequest("");

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/feedback", attempt.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return unauthorized when submitting feedback on assessment attempt without authentication")
    void shouldReturnUnauthorizedWhenSubmittingFeedbackOnAssessmentAttemptWithoutAuthentication() throws Exception {
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner, "https://github.com/acme/project-anonymous-feedback");
        SubmitFeedbackOnAssessmentRequest request = new SubmitFeedbackOnAssessmentRequest(
                "Anonymous user should not submit this feedback."
        );

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/feedback", attempt.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    private UserEntity getUser(String email) {
        return MetricsControllerIntegrationTestSupport.getSeededUser(userJpaRepository, email);
    }

    private AssessmentAttemptEntity createAttempt(UserEntity owner, String repositoryLink) {
        return MetricsControllerIntegrationTestSupport.createFinishedSystemProjectAttempt(
                questionJpaRepository,
                assessmentJpaRepository,
                assessmentAttemptJpaRepository,
                owner,
                repositoryLink
        );
    }
}
