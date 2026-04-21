package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.entities.assessment.AnswerEntity;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.SystemAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.question.ProjectQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
class ListPublicMiniProjectAttemptsEndpointIntegrationTest {

    private static final String DEV_USER_HEADER = "X-Dev-User-Email";

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
    private QuestionMapper questionMapper;

    @AfterEach
    void tearDown() {
        assessmentAttemptJpaRepository.deleteAll();
        assessmentJpaRepository.deleteAll();
        questionJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should list only finished system mini-project attempts from other users")
    void shouldListOnlyFinishedSystemMiniProjectAttemptsFromOtherUsers() throws Exception {
        UserEntity reviewer = getSeededUser("mariana.costa@example.com");
        UserEntity owner = getSeededUser("carlos.silva@example.com");

        AssessmentAttemptEntity visibleAttempt = createFinishedSystemProjectAttempt(
                owner,
                "https://github.com/acme/project-reviewable"
        );
        createUnfinishedSystemProjectAttempt(owner, "https://github.com/acme/project-unfinished");
        createFinishedSystemProjectAttempt(reviewer, "https://github.com/acme/project-own");
        createSystemMultipleChoiceAttempt(owner);

        mockMvc.perform(get("/api/v1/assessment-attempts/mini-project/public")
                        .header(DEV_USER_HEADER, reviewer.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].attemptId").value(visibleAttempt.getId().toString()))
                .andExpect(jsonPath("$[0].repositoryLink").value("https://github.com/acme/project-reviewable"));
    }

    private UserEntity getSeededUser(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seeded user not found: " + email));
    }

    private AssessmentAttemptEntity createFinishedSystemProjectAttempt(UserEntity owner, String repositoryLink) {
        ProjectQuestionEntity projectQuestion = createProjectQuestionEntity();

        SystemAssessmentEntity assessment = new SystemAssessmentEntity();
        assessment.setTitle("System Project Assessment " + UUID.randomUUID());
        assessment.setDescription("System project assessment description");
        assessment.setCreationDate(LocalDateTime.now().minusDays(1));
        assessment.setEvaluationTimerSeconds(3600L);
        assessment.setQuestions(List.of(projectQuestion));
        SystemAssessmentEntity savedAssessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(savedAssessment);
        attempt.setUser(owner);
        attempt.setQuestions(List.of(projectQuestion));
        attempt.setStartedAt(LocalDateTime.now().minusHours(2));
        attempt.setFinishedAt(LocalDateTime.now().minusHours(1));
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        attempt.setAccuracyRate(0);

        AnswerEntity answer = new AnswerEntity();
        answer.setQuestionId(projectQuestion.getId());
        answer.setProjectUrl(repositoryLink);
        attempt.addAnswer(answer);

        return assessmentAttemptJpaRepository.save(attempt);
    }

    private AssessmentAttemptEntity createUnfinishedSystemProjectAttempt(UserEntity owner, String repositoryLink) {
        AssessmentAttemptEntity attempt = createFinishedSystemProjectAttempt(owner, repositoryLink);
        attempt.setFinishedAt(null);
        attempt.setStatus(AssessmentAttemptStatus.IN_PROGRESS);
        return assessmentAttemptJpaRepository.save(attempt);
    }

    private AssessmentAttemptEntity createSystemMultipleChoiceAttempt(UserEntity owner) {
        QuestionEntity multipleChoiceQuestion = createMultipleChoiceQuestion();

        SystemAssessmentEntity assessment = new SystemAssessmentEntity();
        assessment.setTitle("System Multiple Choice " + UUID.randomUUID());
        assessment.setDescription("System multiple choice description");
        assessment.setCreationDate(LocalDateTime.now().minusDays(1));
        assessment.setEvaluationTimerSeconds(1800L);
        assessment.setQuestions(List.of(multipleChoiceQuestion));
        SystemAssessmentEntity savedAssessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(savedAssessment);
        attempt.setUser(owner);
        attempt.setQuestions(List.of(multipleChoiceQuestion));
        attempt.setStartedAt(LocalDateTime.now().minusHours(2));
        attempt.setFinishedAt(LocalDateTime.now().minusHours(1));
        attempt.setStatus(AssessmentAttemptStatus.APPROVED);
        attempt.setAccuracyRate(100);

        AnswerEntity answer = new AnswerEntity();
        answer.setQuestionId(multipleChoiceQuestion.getId());
        answer.setSelectedAlternativeId(UUID.randomUUID());
        attempt.addAnswer(answer);

        return assessmentAttemptJpaRepository.save(attempt);
    }

    private ProjectQuestionEntity createProjectQuestionEntity() {
        ProjectQuestionEntity question = new ProjectQuestionEntity();
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Project Question " + UUID.randomUUID());
        question.setDescription("Project question description " + UUID.randomUUID());
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now().minusDays(2));
        question.setVotingEndDate(LocalDateTime.now().plusDays(2));
        question.setStatus(QuestionStatus.APPROVED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        return questionJpaRepository.save(question);
    }

    private QuestionEntity createMultipleChoiceQuestion() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(
                List.of(
                        new Alternative(null, "Alternative A", false),
                        new Alternative(null, "Alternative B", true)
                )
        );
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Test Question Title - " + UUID.randomUUID());
        question.setDescription("Test question description. - " + UUID.randomUUID());
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setStatus(QuestionStatus.VOTING);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(7));
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        return questionJpaRepository.save(questionMapper.toEntity(question));
    }
}
