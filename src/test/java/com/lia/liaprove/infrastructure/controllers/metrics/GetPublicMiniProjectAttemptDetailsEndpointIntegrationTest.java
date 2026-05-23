package com.lia.liaprove.infrastructure.controllers.metrics;

import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.lia.liaprove.infrastructure.controllers.metrics.MetricsControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.metrics.MetricsControllerIntegrationTestSupport.OTHER_PROFESSIONAL_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.metrics.MetricsControllerIntegrationTestSupport.PROFESSIONAL_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.metrics.MetricsControllerIntegrationTestSupport.createFinishedSystemProjectAttempt;
import static com.lia.liaprove.infrastructure.controllers.metrics.MetricsControllerIntegrationTestSupport.deleteMetricsData;
import static com.lia.liaprove.infrastructure.controllers.metrics.MetricsControllerIntegrationTestSupport.getSeededUser;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(
        scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class GetPublicMiniProjectAttemptDetailsEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
        deleteMetricsData(
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
    @DisplayName("Should get public mini-project attempt details when attempt is visible to reviewer")
    void shouldGetPublicMiniProjectAttemptDetails() throws Exception {
        UserEntity owner = getSeededUser(userJpaRepository, PROFESSIONAL_EMAIL);
        UserEntity reviewer = getSeededUser(userJpaRepository, OTHER_PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createFinishedSystemProjectAttempt(
                questionJpaRepository,
                assessmentJpaRepository,
                assessmentAttemptJpaRepository,
                owner,
                "https://github.com/acme/project-reviewable"
        );

        mockMvc.perform(get("/api/v1/assessment-attempts/mini-project/public/{attemptId}", attempt.getId())
                        .header(DEV_USER_HEADER, reviewer.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attemptId", is(attempt.getId().toString())))
                .andExpect(jsonPath("$.assessmentTitle", is(attempt.getAssessment().getTitle())))
                .andExpect(jsonPath("$.authorName", is(owner.getName())))
                .andExpect(jsonPath("$.repositoryLink", is("https://github.com/acme/project-reviewable")))
                .andExpect(jsonPath("$.question.id", is(attempt.getQuestions().getFirst().getId().toString())))
                .andExpect(jsonPath("$.question.title", is(attempt.getQuestions().getFirst().getTitle())))
                .andExpect(jsonPath("$.voteSummary.approves", is(0)))
                .andExpect(jsonPath("$.voteSummary.rejects", is(0)))
                .andExpect(jsonPath("$.feedbacks").isArray())
                .andExpect(jsonPath("$.feedbacks.length()", is(0)));
    }

    @Test
    @DisplayName("Should return not found when reviewer tries to access own public mini-project attempt")
    void shouldReturnNotFoundForOwnPublicMiniProjectAttempt() throws Exception {
        UserEntity owner = getSeededUser(userJpaRepository, PROFESSIONAL_EMAIL);
        AssessmentAttemptEntity attempt = createFinishedSystemProjectAttempt(
                questionJpaRepository,
                assessmentJpaRepository,
                assessmentAttemptJpaRepository,
                owner,
                "https://github.com/acme/project-own"
        );

        mockMvc.perform(get("/api/v1/assessment-attempts/mini-project/public/{attemptId}", attempt.getId())
                        .header(DEV_USER_HEADER, owner.getEmail()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return not found when public mini-project attempt does not exist")
    void shouldReturnNotFoundWhenPublicMiniProjectAttemptDoesNotExist() throws Exception {
        UserEntity reviewer = getSeededUser(userJpaRepository, OTHER_PROFESSIONAL_EMAIL);

        mockMvc.perform(get("/api/v1/assessment-attempts/mini-project/public/{attemptId}", UUID.randomUUID())
                        .header(DEV_USER_HEADER, reviewer.getEmail()))
                .andExpect(status().isNotFound());
    }
}
