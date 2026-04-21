package com.lia.liaprove.infrastructure.controllers.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.infrastructure.dtos.metrics.UpdateFeedbackCommentRequest;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptVoteJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.FeedbackAssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import com.lia.liaprove.infrastructure.repositories.VoteJpaRepository;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(
        scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class UpdateFeedbackCommentEndpointIntegrationTest {

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

    @Autowired
    private QuestionMapper questionMapper;

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
    @DisplayName("Should update feedback comment successfully")
    void shouldUpdateFeedbackCommentSuccessfully() throws Exception {
        UserEntity author = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        FeedbackQuestionEntity feedback = createFeedback(author);
        UpdateFeedbackCommentRequest request = new UpdateFeedbackCommentRequest("Updated comment text.");

        mockMvc.perform(patch("/api/v1/feedbacks/{feedbackId}", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, author.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        FeedbackQuestionEntity updatedFeedback = feedbackQuestionJpaRepository.findById(feedback.getId()).orElseThrow();
        assertThat(updatedFeedback.getComment()).isEqualTo("Updated comment text.");
        assertThat(updatedFeedback.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should return forbidden when updating another user's feedback comment")
    void shouldReturnForbiddenWhenUpdatingAnotherUsersFeedbackComment() throws Exception {
        UserEntity author = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        UserEntity otherUser = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL
        );
        FeedbackQuestionEntity feedback = createFeedback(author);
        UpdateFeedbackCommentRequest request = new UpdateFeedbackCommentRequest("Unauthorized update attempt.");

        mockMvc.perform(patch("/api/v1/feedbacks/{feedbackId}", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, otherUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return bad request when updated feedback comment is blank")
    void shouldReturnBadRequestWhenUpdatedFeedbackCommentIsBlank() throws Exception {
        UserEntity author = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        FeedbackQuestionEntity feedback = createFeedback(author);
        UpdateFeedbackCommentRequest request = new UpdateFeedbackCommentRequest("");

        mockMvc.perform(patch("/api/v1/feedbacks/{feedbackId}", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, author.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.comment", notNullValue()));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent feedback")
    void shouldReturnNotFoundWhenUpdatingNonExistentFeedback() throws Exception {
        UserEntity author = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        UpdateFeedbackCommentRequest request = new UpdateFeedbackCommentRequest("Trying to update missing feedback.");

        mockMvc.perform(patch("/api/v1/feedbacks/{feedbackId}", UUID.randomUUID())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, author.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return unauthorized when updating feedback without authentication")
    void shouldReturnUnauthorizedWhenUpdatingFeedbackWithoutAuthentication() throws Exception {
        UserEntity author = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        FeedbackQuestionEntity feedback = createFeedback(author);
        UpdateFeedbackCommentRequest request = new UpdateFeedbackCommentRequest("Anonymous update attempt.");

        mockMvc.perform(patch("/api/v1/feedbacks/{feedbackId}", feedback.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    private FeedbackQuestionEntity createFeedback(UserEntity author) {
        QuestionEntity question = MetricsControllerIntegrationTestSupport.createTestQuestion(
                questionJpaRepository,
                questionMapper
        );
        return MetricsControllerIntegrationTestSupport.createTestFeedbackQuestion(
                feedbackQuestionJpaRepository,
                author,
                question
        );
    }
}
