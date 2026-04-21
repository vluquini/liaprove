package com.lia.liaprove.infrastructure.controllers.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.metrics.ReactionType;
import com.lia.liaprove.infrastructure.dtos.metrics.ReactToFeedbackRequest;
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
class ReactToFeedbackEndpointIntegrationTest {

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
    @DisplayName("Should react to feedback successfully with LIKE")
    void shouldReactToFeedbackSuccessfullyWithLike() throws Exception {
        UserEntity author = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        UserEntity reactor = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL
        );
        FeedbackQuestionEntity feedback = createFeedback(author);
        ReactToFeedbackRequest request = MetricsControllerIntegrationTestSupport.likeReactionRequest();

        mockMvc.perform(post("/api/v1/feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reactor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        FeedbackQuestionEntity updatedFeedback = feedbackQuestionJpaRepository
                .findFeedbackByIdWithDetails(feedback.getId())
                .orElseThrow();
        assertThat(updatedFeedback.getReactions()).hasSize(1);
        assertThat(updatedFeedback.getReactions().getFirst().getUser().getId()).isEqualTo(reactor.getId());
        assertThat(updatedFeedback.getReactions().getFirst().getType()).isEqualTo(ReactionType.LIKE);
    }

    @Test
    @DisplayName("Should update reaction successfully from LIKE to DISLIKE")
    void shouldUpdateReactionSuccessfullyFromLikeToDislike() throws Exception {
        UserEntity author = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL
        );
        UserEntity reactor = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.JUNIOR_EMAIL
        );
        FeedbackQuestionEntity feedback = createFeedback(author);

        ReactToFeedbackRequest likeRequest = new ReactToFeedbackRequest();
        likeRequest.setReactionType(ReactionType.LIKE);
        mockMvc.perform(post("/api/v1/feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reactor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeRequest)))
                .andExpect(status().isOk());

        ReactToFeedbackRequest dislikeRequest = new ReactToFeedbackRequest();
        dislikeRequest.setReactionType(ReactionType.DISLIKE);
        mockMvc.perform(post("/api/v1/feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reactor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dislikeRequest)))
                .andExpect(status().isOk());

        FeedbackQuestionEntity updatedFeedback = feedbackQuestionJpaRepository
                .findFeedbackByIdWithDetails(feedback.getId())
                .orElseThrow();
        assertThat(updatedFeedback.getReactions()).hasSize(1);
        assertThat(updatedFeedback.getReactions().getFirst().getUser().getId()).isEqualTo(reactor.getId());
        assertThat(updatedFeedback.getReactions().getFirst().getType()).isEqualTo(ReactionType.DISLIKE);
    }

    @Test
    @DisplayName("Should remove reaction when same type is submitted again")
    void shouldRemoveReactionWhenSameTypeIsSubmittedAgain() throws Exception {
        UserEntity author = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.JUNIOR_EMAIL
        );
        UserEntity reactor = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        FeedbackQuestionEntity feedback = createFeedback(author);
        ReactToFeedbackRequest request = MetricsControllerIntegrationTestSupport.likeReactionRequest();

        mockMvc.perform(post("/api/v1/feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reactor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        FeedbackQuestionEntity feedbackAfterFirstReaction = feedbackQuestionJpaRepository
                .findFeedbackByIdWithDetails(feedback.getId())
                .orElseThrow();
        assertThat(feedbackAfterFirstReaction.getReactions()).hasSize(1);

        mockMvc.perform(post("/api/v1/feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reactor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        FeedbackQuestionEntity updatedFeedback = feedbackQuestionJpaRepository
                .findFeedbackByIdWithDetails(feedback.getId())
                .orElseThrow();
        assertThat(updatedFeedback.getReactions()).isEmpty();
    }

    @Test
    @DisplayName("Should return forbidden when reacting to own feedback")
    void shouldReturnForbiddenWhenReactingToOwnFeedback() throws Exception {
        UserEntity feedbackAuthor = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.ADMIN_EMAIL
        );
        FeedbackQuestionEntity feedback = createFeedback(feedbackAuthor);
        ReactToFeedbackRequest request = MetricsControllerIntegrationTestSupport.likeReactionRequest();

        mockMvc.perform(post("/api/v1/feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, feedbackAuthor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return not found when feedback does not exist")
    void shouldReturnNotFoundWhenFeedbackDoesNotExist() throws Exception {
        UserEntity user = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        ReactToFeedbackRequest request = MetricsControllerIntegrationTestSupport.likeReactionRequest();

        mockMvc.perform(post("/api/v1/feedbacks/{feedbackId}/react", UUID.randomUUID())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return bad request when reaction type is missing")
    void shouldReturnBadRequestWhenReactionTypeIsMissing() throws Exception {
        UserEntity author = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        UserEntity reactor = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL
        );
        FeedbackQuestionEntity feedback = createFeedback(author);
        ReactToFeedbackRequest request = new ReactToFeedbackRequest();

        mockMvc.perform(post("/api/v1/feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reactor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.reactionType", notNullValue()));
    }

    @Test
    @DisplayName("Should return unauthorized when reacting to feedback without authentication")
    void shouldReturnUnauthorizedWhenReactingToFeedbackWithoutAuthentication() throws Exception {
        UserEntity author = MetricsControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        FeedbackQuestionEntity feedback = createFeedback(author);
        ReactToFeedbackRequest request = MetricsControllerIntegrationTestSupport.likeReactionRequest();

        mockMvc.perform(post("/api/v1/feedbacks/{feedbackId}/react", feedback.getId())
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
