package com.lia.liaprove.infrastructure.controllers.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.metrics.AssessmentAttemptVoteEntity;
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
class VoteEndpointsIntegrationTest {

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
    @DisplayName("Should cast a vote on a question successfully")
    void shouldCastVoteOnQuestionSuccessfully() throws Exception {
        UserEntity user = getUser(MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL);
        QuestionEntity question = questionJpaRepository.findAll().getFirst();
        CastVoteRequest request = voteRequest(VoteType.APPROVE);

        mockMvc.perform(post("/api/v1/questions/{questionId}/vote", question.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should cast vote on assessment attempt successfully")
    void shouldCastVoteOnAssessmentAttemptSuccessfully() throws Exception {
        UserEntity reviewer = getUser(MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL);
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner, "https://github.com/acme/project-vote");
        CastVoteRequest request = voteRequest(VoteType.APPROVE);

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/vote", attempt.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<AssessmentAttemptVoteEntity> votes = assessmentAttemptVoteJpaRepository.findAll();
        assertThat(votes).hasSize(1);
        assertThat(votes.getFirst().getAssessmentAttempt().getId()).isEqualTo(attempt.getId());
        assertThat(votes.getFirst().getUser().getId()).isEqualTo(reviewer.getId());
        assertThat(votes.getFirst().getVoteType()).isEqualTo(VoteType.APPROVE);
    }

    @Test
    @DisplayName("Should reject duplicate vote on same assessment attempt")
    void shouldRejectDuplicateVoteOnSameAssessmentAttempt() throws Exception {
        UserEntity reviewer = getUser(MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL);
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner, "https://github.com/acme/project-duplicate-vote");
        CastVoteRequest request = voteRequest(VoteType.APPROVE);

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/vote", attempt.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/vote", attempt.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject vote on own assessment attempt")
    void shouldRejectVoteOnOwnAssessmentAttempt() throws Exception {
        UserEntity owner = getUser(MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createAttempt(owner, "https://github.com/acme/project-own-vote");
        CastVoteRequest request = voteRequest(VoteType.REJECT);

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/vote", attempt.getId())
                        .header(MetricsControllerIntegrationTestSupport.DEV_USER_HEADER, owner.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private UserEntity getUser(String email) {
        return MetricsControllerIntegrationTestSupport.getSeededUser(userJpaRepository, email);
    }

    private CastVoteRequest voteRequest(VoteType voteType) {
        CastVoteRequest request = new CastVoteRequest();
        request.setVoteType(voteType);
        return request;
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
