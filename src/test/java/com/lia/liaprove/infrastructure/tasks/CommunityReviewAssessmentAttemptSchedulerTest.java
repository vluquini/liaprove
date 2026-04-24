package com.lia.liaprove.infrastructure.tasks;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.entities.assessment.AnswerEntity;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.SystemAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.question.ProjectQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.question.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
@Sql(scripts = {
        "classpath:db/h2-populate-users.sql",
        "classpath:db/h2-populate-questions.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CommunityReviewAssessmentAttemptSchedulerTest {

    @Autowired
    private CommunityReviewAssessmentAttemptScheduler scheduler;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;

    @Test
    @DisplayName("Should update expired completed system project attempt to a final status")
    void shouldUpdateExpiredCompletedSystemProjectAttemptToFinalStatus() {
        UserEntity owner = getSeededUserEntity("carlos.silva@example.com");
        AssessmentAttemptEntity expiredAttempt = createSystemProjectAttempt(
                owner,
                AssessmentAttemptStatus.COMPLETED,
                LocalDateTime.now().minusDays(8),
                "https://github.com/acme/project-expired"
        );

        scheduler.evaluateExpiredCommunityReviewAttempts();

        AssessmentAttemptEntity processedAttempt = assessmentAttemptJpaRepository.findById(expiredAttempt.getId()).orElseThrow();
        assertThat(processedAttempt.getStatus()).isIn(AssessmentAttemptStatus.APPROVED, AssessmentAttemptStatus.FAILED);
    }

    @Test
    @DisplayName("Should keep recent completed system project attempt untouched")
    void shouldKeepRecentCompletedSystemProjectAttemptUntouched() {
        UserEntity owner = getSeededUserEntity("carlos.silva@example.com");
        AssessmentAttemptEntity recentAttempt = createSystemProjectAttempt(
                owner,
                AssessmentAttemptStatus.COMPLETED,
                LocalDateTime.now().minusDays(2),
                "https://github.com/acme/project-recent"
        );

        scheduler.evaluateExpiredCommunityReviewAttempts();

        AssessmentAttemptEntity unprocessedAttempt = assessmentAttemptJpaRepository.findById(recentAttempt.getId()).orElseThrow();
        assertThat(unprocessedAttempt.getStatus()).isEqualTo(AssessmentAttemptStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should ignore completed system multiple choice attempt")
    void shouldIgnoreCompletedSystemMultipleChoiceAttempt() {
        UserEntity owner = getSeededUserEntity("carlos.silva@example.com");
        AssessmentAttemptEntity multipleChoiceAttempt = createCompletedSystemMultipleChoiceAttempt(
                owner,
                LocalDateTime.now().minusDays(8)
        );

        scheduler.evaluateExpiredCommunityReviewAttempts();

        AssessmentAttemptEntity unprocessedAttempt = assessmentAttemptJpaRepository.findById(multipleChoiceAttempt.getId()).orElseThrow();
        assertThat(unprocessedAttempt.getStatus()).isEqualTo(AssessmentAttemptStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should ignore already decided system project attempt")
    void shouldIgnoreAlreadyDecidedSystemProjectAttempt() {
        UserEntity owner = getSeededUserEntity("carlos.silva@example.com");
        AssessmentAttemptEntity approvedAttempt = createSystemProjectAttempt(
                owner,
                AssessmentAttemptStatus.APPROVED,
                LocalDateTime.now().minusDays(8),
                "https://github.com/acme/project-approved"
        );

        scheduler.evaluateExpiredCommunityReviewAttempts();

        AssessmentAttemptEntity unprocessedAttempt = assessmentAttemptJpaRepository.findById(approvedAttempt.getId()).orElseThrow();
        assertThat(unprocessedAttempt.getStatus()).isEqualTo(AssessmentAttemptStatus.APPROVED);
    }

    private UserEntity getSeededUserEntity(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seeded user not found: " + email));
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

    private AssessmentAttemptEntity createSystemProjectAttempt(
            UserEntity owner,
            AssessmentAttemptStatus status,
            LocalDateTime finishedAt,
            String repositoryLink
    ) {
        ProjectQuestionEntity projectQuestion = createProjectQuestionEntity();

        SystemAssessmentEntity assessment = new SystemAssessmentEntity();
        assessment.setTitle("System Project Assessment " + UUID.randomUUID());
        assessment.setDescription("System project assessment description");
        assessment.setCreationDate(LocalDateTime.now().minusDays(10));
        assessment.setEvaluationTimerSeconds(3600L);
        assessment.setQuestions(new ArrayList<>(List.of(projectQuestion)));
        SystemAssessmentEntity savedAssessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(savedAssessment);
        attempt.setUser(owner);
        attempt.setQuestions(new ArrayList<>(List.of(projectQuestion)));
        attempt.setStartedAt(finishedAt.minusHours(1));
        attempt.setFinishedAt(finishedAt);
        attempt.setStatus(status);
        attempt.setAccuracyRate(0);

        AnswerEntity answer = new AnswerEntity();
        answer.setQuestionId(projectQuestion.getId());
        answer.setProjectUrl(repositoryLink);
        attempt.addAnswer(answer);

        return assessmentAttemptJpaRepository.save(attempt);
    }

    private AssessmentAttemptEntity createCompletedSystemMultipleChoiceAttempt(UserEntity owner, LocalDateTime finishedAt) {
        QuestionEntity multipleChoiceQuestion = questionJpaRepository.findAll().stream()
                .filter(question -> !(question instanceof ProjectQuestionEntity))
                .findFirst()
                .orElseThrow();

        SystemAssessmentEntity assessment = new SystemAssessmentEntity();
        assessment.setTitle("System Multiple Choice Assessment " + UUID.randomUUID());
        assessment.setDescription("System multiple choice assessment description");
        assessment.setCreationDate(LocalDateTime.now().minusDays(10));
        assessment.setEvaluationTimerSeconds(1800L);
        assessment.setQuestions(new ArrayList<>(List.of(multipleChoiceQuestion)));
        SystemAssessmentEntity savedAssessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(savedAssessment);
        attempt.setUser(owner);
        attempt.setQuestions(new ArrayList<>(List.of(multipleChoiceQuestion)));
        attempt.setStartedAt(finishedAt.minusMinutes(30));
        attempt.setFinishedAt(finishedAt);
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        attempt.setAccuracyRate(100);

        AnswerEntity answer = new AnswerEntity();
        answer.setQuestionId(multipleChoiceQuestion.getId());
        answer.setSelectedAlternativeId(UUID.randomUUID());
        attempt.addAnswer(answer);

        return assessmentAttemptJpaRepository.save(attempt);
    }
}
