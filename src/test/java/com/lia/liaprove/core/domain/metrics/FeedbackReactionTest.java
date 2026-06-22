package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackReactionTest {

    @Test
    void shouldCreateReactionForFeedback() {
        User user = user(UUID.randomUUID(), "reactor@example.com");
        Feedback feedback = feedback();

        FeedbackReaction reaction = new FeedbackReaction(user, feedback, ReactionType.LIKE);

        assertThat(reaction.getUser()).isEqualTo(user);
        assertThat(reaction.getFeedback()).isSameAs(feedback);
        assertThat(reaction.getType()).isEqualTo(ReactionType.LIKE);
        assertThat(reaction.getCreatedAt()).isNotNull();
        assertThat(reaction.getUpdatedAt()).isEqualTo(reaction.getCreatedAt());
    }

    @Test
    void shouldRejectNullConstructorArguments() {
        User user = user(UUID.randomUUID(), "reactor@example.com");
        Feedback feedback = feedback();

        assertThatThrownBy(() -> new FeedbackReaction(null, feedback, ReactionType.LIKE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new FeedbackReaction(user, null, ReactionType.LIKE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new FeedbackReaction(user, feedback, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldPreventChangingIdentityFieldsAfterTheyAreDefined() {
        User user = user(UUID.randomUUID(), "reactor@example.com");
        Feedback feedback = feedback();
        FeedbackReaction reaction = new FeedbackReaction(user, feedback, ReactionType.LIKE);
        reaction.setId(UUID.randomUUID());
        LocalDateTime createdAt = reaction.getCreatedAt();

        assertThatThrownBy(() -> reaction.setId(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> reaction.setUser(user(UUID.randomUUID(), "other@example.com")))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> reaction.setFeedback(feedback()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> reaction.setCreatedAt(createdAt.plusDays(1)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldRejectNullReactionType() {
        FeedbackReaction reaction = new FeedbackReaction(
                user(UUID.randomUUID(), "reactor@example.com"),
                feedback(),
                ReactionType.LIKE
        );

        assertThatThrownBy(() -> reaction.setType(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldUpdateTimestampWhenTypeChanges() {
        FeedbackReaction reaction = new FeedbackReaction(
                user(UUID.randomUUID(), "reactor@example.com"),
                feedback(),
                ReactionType.LIKE
        );
        LocalDateTime previousUpdatedAt = reaction.getUpdatedAt();

        reaction.setType(ReactionType.DISLIKE);

        assertThat(reaction.getType()).isEqualTo(ReactionType.DISLIKE);
        assertThat(reaction.getUpdatedAt()).isAfterOrEqualTo(previousUpdatedAt);
    }

    @Test
    void shouldCompareByIdSafelyWhenIdsAreNull() {
        FeedbackReaction first = new FeedbackReaction();
        FeedbackReaction second = new FeedbackReaction();

        assertThatCode(() -> first.equals(second)).doesNotThrowAnyException();
    }

    private static Feedback feedback() {
        return new TestFeedback(
                user(UUID.randomUUID(), "author@example.com"),
                "original comment",
                LocalDateTime.of(2026, 1, 1, 9, 0),
                true
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

    private static final class TestFeedback extends Feedback {
        private TestFeedback(User user, String comment, LocalDateTime submissionDate, boolean visible) {
            super(user, comment, submissionDate, visible);
        }
    }
}
