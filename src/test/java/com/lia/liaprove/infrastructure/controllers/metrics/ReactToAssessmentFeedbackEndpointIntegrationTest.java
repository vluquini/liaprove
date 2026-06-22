package com.lia.liaprove.infrastructure.controllers.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.metrics.ReactionType;
import com.lia.liaprove.infrastructure.dtos.metrics.ReactToFeedbackRequest;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackReactionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptVoteJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.FeedbackAssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.VoteJpaRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(
        scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class ReactToAssessmentFeedbackEndpointIntegrationTest {

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

    @Autowired
    private AssessmentAttemptVoteJpaRepository assessmentAttemptVoteJpaRepository;

    @Autowired
    private VoteJpaRepository voteJpaRepository;

    @Autowired
    private FeedbackAssessmentJpaRepository feedbackAssessmentJpaRepository;

    @Autowired
    private FeedbackQuestionJpaRepository feedbackQuestionJpaRepository;

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
    @DisplayName("Should react to assessment feedback successfully with LIKE")
    void shouldReactToAssessmentFeedbackSuccessfullyWithLike() throws Exception {
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        UserEntity feedbackAuthor = getUser(MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL);
        UserEntity reactor = getUser(MetricsControllerIntegrationTestSupport.JUNIOR_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner);
        FeedbackAssessmentEntity feedback = createFeedback(feedbackAuthor, attempt);
        ReactToFeedbackRequest request = reactionRequest(ReactionType.LIKE);

        mockMvc.perform(post("/api/v1/assessment-feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reactor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<FeedbackReactionEntity> reactions = findReactionsByFeedbackId(feedback.getId());
        assertThat(reactions).hasSize(1);
        assertThat(reactions.getFirst().getUser().getId()).isEqualTo(reactor.getId());
        assertThat(reactions.getFirst().getType()).isEqualTo(ReactionType.LIKE);
    }

    @Test
    @DisplayName("Should remove assessment feedback reaction when same type is submitted again")
    void shouldRemoveAssessmentFeedbackReactionWhenSameTypeIsSubmittedAgain() throws Exception {
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        UserEntity feedbackAuthor = getUser(MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL);
        UserEntity reactor = getUser(MetricsControllerIntegrationTestSupport.JUNIOR_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner);
        FeedbackAssessmentEntity feedback = createFeedback(feedbackAuthor, attempt);
        ReactToFeedbackRequest request = reactionRequest(ReactionType.LIKE);

        mockMvc.perform(post("/api/v1/assessment-feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reactor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/assessment-feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reactor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertThat(findReactionsByFeedbackId(feedback.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should return forbidden when reacting to own assessment feedback")
    void shouldReturnForbiddenWhenReactingToOwnAssessmentFeedback() throws Exception {
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        UserEntity feedbackAuthor = getUser(MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner);
        FeedbackAssessmentEntity feedback = createFeedback(feedbackAuthor, attempt);
        ReactToFeedbackRequest request = reactionRequest(ReactionType.LIKE);

        mockMvc.perform(post("/api/v1/assessment-feedbacks/{feedbackId}/react", feedback.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, feedbackAuthor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return not found when assessment feedback does not exist")
    void shouldReturnNotFoundWhenAssessmentFeedbackDoesNotExist() throws Exception {
        UserEntity reactor = getUser(MetricsControllerIntegrationTestSupport.JUNIOR_EMAIL);
        ReactToFeedbackRequest request = reactionRequest(ReactionType.LIKE);

        mockMvc.perform(post("/api/v1/assessment-feedbacks/{feedbackId}/react", UUID.randomUUID())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reactor.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    private UserEntity getUser(String email) {
        return MetricsControllerIntegrationTestSupport.getSeededUser(userJpaRepository, email);
    }

    private AssessmentAttemptEntity createAttempt(UserEntity owner) {
        return MetricsControllerIntegrationTestSupport.createFinishedSystemProjectAttempt(
                questionJpaRepository,
                assessmentJpaRepository,
                assessmentAttemptJpaRepository,
                owner,
                "https://github.com/acme/project-assessment-feedback-reaction"
        );
    }

    private FeedbackAssessmentEntity createFeedback(UserEntity author, AssessmentAttemptEntity attempt) {
        FeedbackAssessmentEntity feedback = new FeedbackAssessmentEntity();
        feedback.setUser(author);
        feedback.setAssessmentAttemptId(attempt.getId());
        feedback.setComment("Assessment feedback to react to.");
        feedback.setSubmissionDate(LocalDateTime.now().minusMinutes(10));
        feedback.setVisible(true);
        return feedbackAssessmentJpaRepository.save(feedback);
    }

    private List<FeedbackReactionEntity> findReactionsByFeedbackId(UUID feedbackId) {
        return feedbackAssessmentJpaRepository.findByIdWithDetails(feedbackId)
                .map(FeedbackAssessmentEntity::getReactions)
                .orElse(List.of());
    }

    private ReactToFeedbackRequest reactionRequest(ReactionType reactionType) {
        ReactToFeedbackRequest request = new ReactToFeedbackRequest();
        request.setReactionType(reactionType);
        return request;
    }
}
