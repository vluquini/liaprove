package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.metrics.AssessmentAttemptVoteGateway;
import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.SystemAssessment;
import com.lia.liaprove.core.domain.metrics.AssessmentAttemptVote;
import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.metrics.FeedbackReaction;
import com.lia.liaprove.core.domain.metrics.ReactionType;
import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPublicMiniProjectAttemptDetailsUseCaseImplTest {

    @Mock
    private AssessmentAttemptGateway assessmentAttemptGateway;

    @Mock
    private AssessmentAttemptVoteGateway assessmentAttemptVoteGateway;

    @Mock
    private FeedbackGateway feedbackGateway;

    @Test
    void shouldReturnPublicMiniProjectAttemptDetailsWhenAttemptIsEligible() {
        UUID currentUserId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        ProjectQuestion projectQuestion = projectQuestion();
        AssessmentAttempt attempt = finishedProjectAttempt(attemptId, projectQuestion, "https://github.com/acme/api");

        when(assessmentAttemptGateway.findPublicSystemProjectAttemptDetailsExcludingUser(attemptId, currentUserId))
                .thenReturn(Optional.of(attempt));

        GetPublicMiniProjectAttemptDetailsUseCaseImpl useCase =
                new GetPublicMiniProjectAttemptDetailsUseCaseImpl(
                        assessmentAttemptGateway,
                        assessmentAttemptVoteGateway,
                        feedbackGateway
                );

        Optional<PublicMiniProjectAttemptDetails> result = useCase.execute(attemptId, currentUserId);

        assertThat(result).isPresent();
        PublicMiniProjectAttemptDetails details = result.orElseThrow();
        assertThat(details.attempt()).isSameAs(attempt);
        assertThat(details.question()).isSameAs(projectQuestion);
        assertThat(details.repositoryLink()).isEqualTo("https://github.com/acme/api");
        assertThat(details.textResponse()).isNull();
        assertThat(details.approveVotes()).isZero();
        assertThat(details.rejectVotes()).isZero();
        assertThat(details.feedbacks()).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenAttemptDoesNotExistOrIsNotPubliclyVisible() {
        UUID currentUserId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        when(assessmentAttemptGateway.findPublicSystemProjectAttemptDetailsExcludingUser(attemptId, currentUserId))
                .thenReturn(Optional.empty());

        GetPublicMiniProjectAttemptDetailsUseCaseImpl useCase =
                new GetPublicMiniProjectAttemptDetailsUseCaseImpl(
                        assessmentAttemptGateway,
                        assessmentAttemptVoteGateway,
                        feedbackGateway
                );

        Optional<PublicMiniProjectAttemptDetails> result = useCase.execute(attemptId, currentUserId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnVoteSummaryAndAssessmentFeedbacksWhenAttemptHasCommunityActivity() {
        UUID currentUserId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        ProjectQuestion projectQuestion = projectQuestion();
        AssessmentAttempt attempt = finishedProjectAttempt(attemptId, projectQuestion, "https://github.com/acme/api");
        UserProfessional reviewer = professional(UUID.randomUUID(), "Reviewer");
        UserProfessional reactor = professional(UUID.randomUUID(), "Reactor");
        FeedbackAssessment feedback = feedback(reviewer, attempt, "Good implementation.");
        feedback.react(reactor, ReactionType.LIKE);

        when(assessmentAttemptGateway.findPublicSystemProjectAttemptDetailsExcludingUser(attemptId, currentUserId))
                .thenReturn(Optional.of(attempt));
        when(assessmentAttemptVoteGateway.findByAttemptId(attemptId))
                .thenReturn(List.of(
                        vote(attempt, VoteType.APPROVE),
                        vote(attempt, VoteType.APPROVE),
                        vote(attempt, VoteType.REJECT)
                ));
        when(feedbackGateway.findAssessmentFeedbacksByAttemptId(attemptId))
                .thenReturn(List.of(feedback));

        GetPublicMiniProjectAttemptDetailsUseCaseImpl useCase =
                new GetPublicMiniProjectAttemptDetailsUseCaseImpl(
                        assessmentAttemptGateway,
                        assessmentAttemptVoteGateway,
                        feedbackGateway
                );

        Optional<PublicMiniProjectAttemptDetails> result = useCase.execute(attemptId, currentUserId);

        assertThat(result).isPresent();
        PublicMiniProjectAttemptDetails details = result.orElseThrow();
        assertThat(details.approveVotes()).isEqualTo(2);
        assertThat(details.rejectVotes()).isEqualTo(1);
        assertThat(details.feedbacks()).containsExactly(feedback);
        assertThat(details.feedbacks().getFirst().getReactions()).hasSize(1);
        assertThat(details.feedbacks().getFirst().getReactions().getFirst())
                .isInstanceOf(FeedbackReaction.class);
    }

    @Test
    void shouldReturnEmptyWhenAttemptHasNoProjectAnswer() {
        UUID currentUserId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        ProjectQuestion projectQuestion = projectQuestion();
        AssessmentAttempt attempt = finishedProjectAttempt(attemptId, projectQuestion, "");

        when(assessmentAttemptGateway.findPublicSystemProjectAttemptDetailsExcludingUser(attemptId, currentUserId))
                .thenReturn(Optional.of(attempt));

        GetPublicMiniProjectAttemptDetailsUseCaseImpl useCase =
                new GetPublicMiniProjectAttemptDetailsUseCaseImpl(
                        assessmentAttemptGateway,
                        assessmentAttemptVoteGateway,
                        feedbackGateway
                );

        Optional<PublicMiniProjectAttemptDetails> result = useCase.execute(attemptId, currentUserId);

        assertThat(result).isEmpty();
    }

    private AssessmentAttempt finishedProjectAttempt(UUID attemptId, ProjectQuestion question, String projectUrl) {
        SystemAssessment assessment = new SystemAssessment(
                UUID.randomUUID(),
                "Assessment title",
                "Assessment description",
                LocalDateTime.now().minusDays(3),
                List.of(question),
                Duration.ofMinutes(60),
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM
        );
        UserProfessional author = professional(UUID.randomUUID());
        Answer answer = new Answer(question.getId());
        answer.setProjectUrl(projectUrl);

        return new AssessmentAttempt(
                attemptId,
                assessment,
                author,
                List.of(question),
                List.of(answer),
                List.of(),
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                0,
                null,
                AssessmentAttemptStatus.COMPLETED
        );
    }

    private ProjectQuestion projectQuestion() {
        return new ProjectQuestion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Build a REST API",
                "Implement a REST API with persistence and tests.",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FOUR,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(5),
                QuestionStatus.APPROVED,
                RelevanceLevel.FOUR,
                0
        );
    }

    private UserProfessional professional(UUID userId) {
        return professional(userId, "Professional");
    }

    private UserProfessional professional(UUID userId, String name) {
        return new UserProfessional(
                userId,
                name,
                "%s@example.com".formatted(name.toLowerCase()),
                "hashed-password",
                "Developer",
                "Bio",
                ExperienceLevel.JUNIOR,
                UserRole.PROFESSIONAL,
                1,
                0,
                List.of(),
                0.0f,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now()
        );
    }

    private AssessmentAttemptVote vote(AssessmentAttempt attempt, VoteType voteType) {
        return new AssessmentAttemptVote(professional(UUID.randomUUID()), attempt, voteType);
    }

    private FeedbackAssessment feedback(UserProfessional user, AssessmentAttempt attempt, String comment) {
        FeedbackAssessment feedback = new FeedbackAssessment(
                user,
                attempt,
                comment,
                LocalDateTime.now().minusMinutes(30),
                true
        );
        feedback.setId(UUID.randomUUID());
        return feedback;
    }
}
