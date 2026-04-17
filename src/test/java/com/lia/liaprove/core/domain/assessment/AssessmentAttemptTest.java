package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentAttemptTest {

    @Test
    void shouldTreatProjectUrlAsManualSubmissionForSystemAssessment() {
        ProjectQuestion question = projectQuestion();
        SystemAssessment assessment = new SystemAssessment(
                UUID.randomUUID(),
                "System project assessment",
                "desc",
                LocalDateTime.now(),
                List.of(question),
                Duration.ofHours(1)
        );
        AssessmentAttempt attempt = new AssessmentAttempt(
                UUID.randomUUID(),
                assessment,
                user(),
                List.of(question),
                null,
                null,
                LocalDateTime.now().minusMinutes(10),
                null,
                null,
                null,
                AssessmentAttemptStatus.IN_PROGRESS
        );

        Answer answer = new Answer(question.getId());
        answer.setProjectUrl("https://github.com/acme/project");

        attempt.finish(List.of(answer));

        assertThat(attempt.getStatus()).isEqualTo(AssessmentAttemptStatus.COMPLETED);
        assertThat(attempt.getAccuracyRate()).isEqualTo(0);
    }

    @Test
    void shouldTreatTextResponseAsManualSubmissionForPersonalizedAssessment() {
        OpenQuestion question = openQuestion();
        PersonalizedAssessment assessment = new PersonalizedAssessment(
                UUID.randomUUID(),
                "Personalized assessment",
                "desc",
                LocalDateTime.now(),
                List.of(question),
                Duration.ofHours(1),
                null,
                LocalDateTime.now().plusDays(1),
                0,
                3,
                "share-token",
                PersonalizedAssessmentStatus.ACTIVE
        );
        AssessmentAttempt attempt = new AssessmentAttempt(
                UUID.randomUUID(),
                assessment,
                user(),
                List.of(question),
                null,
                null,
                LocalDateTime.now().minusMinutes(10),
                null,
                null,
                null,
                AssessmentAttemptStatus.IN_PROGRESS
        );

        Answer answer = new Answer(question.getId());
        answer.setTextResponse("text response for open question");

        attempt.finish(List.of(answer));

        assertThat(attempt.getStatus()).isEqualTo(AssessmentAttemptStatus.COMPLETED);
        assertThat(attempt.getAccuracyRate()).isEqualTo(0);
    }

    private User user() {
        User user = new TestUser();
        user.setId(UUID.randomUUID());
        user.setName("Candidate");
        user.setEmail("candidate@example.com");
        user.setPasswordHash("hashed");
        user.setExperienceLevel(ExperienceLevel.JUNIOR);
        user.setRole(UserRole.PROFESSIONAL);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private ProjectQuestion projectQuestion() {
        ProjectQuestion question = new ProjectQuestion();
        question.setId(UUID.randomUUID());
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Project question");
        question.setDescription("Project description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(2));
        question.setStatus(QuestionStatus.APPROVED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        return question;
    }

    private OpenQuestion openQuestion() {
        OpenQuestion question = new OpenQuestion("Guideline", OpenQuestionVisibility.SHARED);
        question.setId(UUID.randomUUID());
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Open question");
        question.setDescription("Open description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(2));
        question.setStatus(QuestionStatus.APPROVED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        return question;
    }

    private static final class TestUser extends User {
    }
}
