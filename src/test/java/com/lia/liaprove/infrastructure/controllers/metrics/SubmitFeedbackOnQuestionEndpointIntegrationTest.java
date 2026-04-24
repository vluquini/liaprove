package com.lia.liaprove.infrastructure.controllers.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.infrastructure.dtos.metrics.SubmitFeedbackQuestionRequest;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
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
import static org.hamcrest.Matchers.notNullValue;
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
class SubmitFeedbackOnQuestionEndpointIntegrationTest {

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
    @DisplayName("Should submit feedback on a question successfully")
    void shouldSubmitFeedbackOnQuestionSuccessfully() throws Exception {
        UserEntity user = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        QuestionEntity question = questionJpaRepository.findAll().getFirst();
        SubmitFeedbackQuestionRequest request = MetricsControllerIntegrationTestSupport.validQuestionFeedbackRequest();

        mockMvc.perform(post("/api/v1/questions/{questionId}/feedback", question.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        List<FeedbackQuestionEntity> feedbacks = feedbackQuestionJpaRepository.findWithDetailsByQuestionId(question.getId());
        assertThat(feedbacks).hasSize(1);
        assertThat(feedbacks.getFirst().getComment()).isEqualTo("This is a new feedback from an integration test.");
        assertThat(feedbacks.getFirst().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Should return bad request when question feedback payload is invalid")
    void shouldReturnBadRequestWhenQuestionFeedbackPayloadIsInvalid() throws Exception {
        UserEntity user = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        QuestionEntity question = questionJpaRepository.findAll().getFirst();
        SubmitFeedbackQuestionRequest request = MetricsControllerIntegrationTestSupport.invalidQuestionFeedbackRequest();

        mockMvc.perform(post("/api/v1/questions/{questionId}/feedback", question.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.comment", notNullValue()));
    }

    @Test
    @DisplayName("Should return not found when submitting feedback to non-existent question")
    void shouldReturnNotFoundWhenSubmittingFeedbackToNonExistentQuestion() throws Exception {
        UserEntity user = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        SubmitFeedbackQuestionRequest request = MetricsControllerIntegrationTestSupport.validQuestionFeedbackRequest();

        mockMvc.perform(post("/api/v1/questions/{questionId}/feedback", UUID.randomUUID())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return unauthorized when submitting question feedback without authentication")
    void shouldReturnUnauthorizedWhenSubmittingQuestionFeedbackWithoutAuthentication() throws Exception {
        QuestionEntity question = questionJpaRepository.findAll().getFirst();
        SubmitFeedbackQuestionRequest request = MetricsControllerIntegrationTestSupport.validQuestionFeedbackRequest();

        mockMvc.perform(post("/api/v1/questions/{questionId}/feedback", question.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
