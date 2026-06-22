package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackAssessmentTest {

    @Test
    void shouldInitializeAssessmentFeedbackSpecificData() {
        User author = user(UUID.randomUUID(), "author@example.com");
        AssessmentAttempt attempt = assessmentAttempt(UUID.randomUUID(), author);
        LocalDateTime submittedAt = LocalDateTime.of(2026, 1, 2, 10, 0);

        FeedbackAssessment feedback = new FeedbackAssessment(author, attempt, "Private comment", submittedAt, false);

        assertThat(feedback.getUser()).isEqualTo(author);
        assertThat(feedback.getAssessmentAttempt()).isEqualTo(attempt);
        assertThat(feedback.getComment()).isEqualTo("Private comment");
        assertThat(feedback.getSubmissionDate()).isEqualTo(submittedAt);
        assertThat(feedback.isVisible()).isFalse();
    }

    @Test
    void shouldPreventChangingAssessmentAttemptAfterItIsDefined() {
        User author = user(UUID.randomUUID(), "author@example.com");
        FeedbackAssessment feedback = new FeedbackAssessment(
                author,
                assessmentAttempt(UUID.randomUUID(), author),
                "Private comment",
                LocalDateTime.of(2026, 1, 2, 10, 0),
                false
        );

        assertThatThrownBy(() -> feedback.setAssessmentAttempt(assessmentAttempt(UUID.randomUUID(), author)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldUseFeedbackReactionApiWithoutDeclaringAssessmentSpecificReactionMethods() {
        User author = user(UUID.randomUUID(), "author@example.com");
        FeedbackAssessment feedback = new FeedbackAssessment(
                author,
                assessmentAttempt(UUID.randomUUID(), author),
                "Private comment",
                LocalDateTime.of(2026, 1, 2, 10, 0),
                false
        );
        User reactor = user(UUID.randomUUID(), "reactor@example.com");

        feedback.react(reactor, ReactionType.DISLIKE);

        assertThat(feedback.getReactions())
                .singleElement()
                .satisfies(reaction -> {
                    assertThat(reaction.getFeedback()).isSameAs(feedback);
                    assertThat(reaction.getUser()).isEqualTo(reactor);
                    assertThat(reaction.getType()).isEqualTo(ReactionType.DISLIKE);
                });
        assertThat(declaredMethodNames())
                .doesNotContain("manageReaction", "getReactions", "setReactions");
    }

    private static List<String> declaredMethodNames() {
        return Arrays.stream(FeedbackAssessment.class.getDeclaredMethods())
                .map(Method::getName)
                .toList();
    }

    private static AssessmentAttempt assessmentAttempt(UUID id, User user) {
        return new AssessmentAttempt(
                id,
                null,
                user,
                List.of(),
                List.of(),
                List.of(),
                LocalDateTime.of(2026, 1, 1, 9, 0),
                null,
                0,
                null,
                AssessmentAttemptStatus.IN_PROGRESS
        );
    }

    private static User user(UUID id, String email) {
        return new UserProfessional(
                id,
                "User",
                email,
                "password",
                "Developer",
                "Bio",
                ExperienceLevel.JUNIOR,
                UserRole.PROFESSIONAL,
                1,
                0,
                List.of(),
                0.0f,
                LocalDateTime.of(2026, 1, 1, 8, 0),
                null
        );
    }
}
