package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.Question;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssessmentAttemptTest {

    @Test
    void shouldApproveSystemAssessmentWhenAccuracyRateIsAtLeastSeventy() {
        QuestionAnswerPair question = multipleChoiceQuestion();
        AssessmentAttempt attempt = systemAttempt(List.of(question.question()));

        attempt.finish(List.of(Answer.multipleChoice(question.question().getId(), question.correctAlternativeId())));

        assertThat(attempt.getStatus()).isEqualTo(AssessmentAttemptStatus.APPROVED);
        assertThat(attempt.getAccuracyRate()).isEqualTo(100);
        assertThat(attempt.getFinishedAt()).isNotNull();
    }

    @Test
    void shouldFailSystemAssessmentWhenAccuracyRateIsBelowSeventy() {
        QuestionAnswerPair question = multipleChoiceQuestion();
        AssessmentAttempt attempt = systemAttempt(List.of(question.question()));

        attempt.finish(List.of(Answer.multipleChoice(question.question().getId(), UUID.randomUUID())));

        assertThat(attempt.getStatus()).isEqualTo(AssessmentAttemptStatus.FAILED);
        assertThat(attempt.getAccuracyRate()).isZero();
    }

    @Test
    void shouldCountMissingAnswersAsWrong() {
        QuestionAnswerPair firstQuestion = multipleChoiceQuestion();
        QuestionAnswerPair secondQuestion = multipleChoiceQuestion();
        AssessmentAttempt attempt = systemAttempt(List.of(firstQuestion.question(), secondQuestion.question()));

        attempt.finish(List.of(Answer.multipleChoice(firstQuestion.question().getId(), firstQuestion.correctAlternativeId())));

        assertThat(attempt.getStatus()).isEqualTo(AssessmentAttemptStatus.FAILED);
        assertThat(attempt.getAccuracyRate()).isEqualTo(50);
    }

    @Test
    void shouldTreatProjectUrlAsManualSubmissionForSystemAssessment() {
        ProjectQuestion question = projectQuestion();
        AssessmentAttempt attempt = systemAttempt(List.of(question));

        attempt.finish(List.of(Answer.project(question.getId(), "https://github.com/acme/project")));

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
                3,
                "share-token",
                PersonalizedAssessmentStatus.ACTIVE,
                AssessmentCriteriaWeights.defaultWeights()
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

        attempt.finish(List.of(Answer.openText(question.getId(), "text response for open question")));

        assertThat(attempt.getStatus()).isEqualTo(AssessmentAttemptStatus.COMPLETED);
        assertThat(attempt.getAccuracyRate()).isEqualTo(0);
    }

    @Test
    void shouldRejectDuplicatedQuestionsInAttempt() {
        QuestionAnswerPair question = multipleChoiceQuestion();

        assertThatThrownBy(() -> systemAttempt(List.of(question.question(), question.question())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("questions must not contain duplicated ids");
    }

    @Test
    void shouldRejectDuplicatedAnswersWhenFinishingAttempt() {
        QuestionAnswerPair question = multipleChoiceQuestion();
        AssessmentAttempt attempt = systemAttempt(List.of(question.question()));
        Answer firstAnswer = Answer.multipleChoice(question.question().getId(), question.correctAlternativeId());
        Answer secondAnswer = Answer.multipleChoice(question.question().getId(), question.correctAlternativeId());

        assertThatThrownBy(() -> attempt.finish(List.of(firstAnswer, secondAnswer)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("submittedAnswers must not contain duplicated question ids");
    }

    @Test
    void shouldRejectAnswerForQuestionOutsideAttempt() {
        QuestionAnswerPair question = multipleChoiceQuestion();
        AssessmentAttempt attempt = systemAttempt(List.of(question.question()));

        assertThatThrownBy(() -> attempt.finish(List.of(Answer.multipleChoice(UUID.randomUUID(), question.correctAlternativeId()))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("submittedAnswers contains answer for question outside this attempt");
    }

    @Test
    void shouldApproveCompletedAttemptThroughExplicitTransition() {
        QuestionAnswerPair question = multipleChoiceQuestion();
        AssessmentAttempt attempt = systemAttempt(List.of(question.question()));
        attempt.complete();

        attempt.approve();

        assertThat(attempt.getStatus()).isEqualTo(AssessmentAttemptStatus.APPROVED);
    }

    @Test
    void shouldRejectApprovalWhenAttemptIsNotCompleted() {
        QuestionAnswerPair question = multipleChoiceQuestion();
        AssessmentAttempt attempt = systemAttempt(List.of(question.question()));

        assertThatThrownBy(attempt::approve)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only completed attempts can be approved");
    }

    @Test
    void shouldFailCompletedAttemptThroughExplicitTransition() {
        QuestionAnswerPair question = multipleChoiceQuestion();
        AssessmentAttempt attempt = systemAttempt(List.of(question.question()));
        attempt.complete();

        attempt.fail();

        assertThat(attempt.getStatus()).isEqualTo(AssessmentAttemptStatus.FAILED);
    }

    private AssessmentAttempt systemAttempt(List<Question> questions) {
        return new AssessmentAttempt(
                UUID.randomUUID(),
                new SystemAssessment(
                        UUID.randomUUID(),
                        "System assessment",
                        "desc",
                        LocalDateTime.now(),
                        questions,
                        Duration.ofHours(1)
                ),
                user(),
                questions,
                null,
                null,
                LocalDateTime.now().minusMinutes(10),
                null,
                null,
                null,
                AssessmentAttemptStatus.IN_PROGRESS
        );
    }

    private QuestionAnswerPair multipleChoiceQuestion() {
        UUID correctAlternativeId = UUID.randomUUID();
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(List.of(
                new Alternative(correctAlternativeId, "Correct", true),
                new Alternative(UUID.randomUUID(), "Wrong", false)
        ));
        question.setId(UUID.randomUUID());
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Multiple choice question");
        question.setDescription("Multiple choice description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(2));
        question.setStatus(QuestionStatus.APPROVED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        return new QuestionAnswerPair(question, correctAlternativeId);
    }

    private record QuestionAnswerPair(MultipleChoiceQuestion question, UUID correctAlternativeId) {
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
