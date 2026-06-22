package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackTest {

    @Test
    void shouldAddReactionWhenUserHasNotReacted() {
        TestFeedback feedback = feedback();
        User reactor = user(UUID.randomUUID(), "reactor@example.com");

        boolean changed = feedback.react(reactor, ReactionType.LIKE);

        assertThat(changed).isTrue();
        assertThat(feedback.getReactions()).hasSize(1);
        FeedbackReaction reaction = feedback.getReactions().getFirst();
        assertThat(reaction.getUser()).isEqualTo(reactor);
        assertThat(reaction.getFeedback()).isSameAs(feedback);
        assertThat(reaction.getType()).isEqualTo(ReactionType.LIKE);
        assertThat(feedback.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldReplaceExistingReactionWhenUserChoosesDifferentType() {
        TestFeedback feedback = feedback();
        User reactor = user(UUID.randomUUID(), "reactor@example.com");

        feedback.react(reactor, ReactionType.LIKE);
        FeedbackReaction createdReaction = feedback.getReactions().getFirst();

        boolean changed = feedback.react(reactor, ReactionType.DISLIKE);

        assertThat(changed).isTrue();
        assertThat(feedback.getReactions())
                .singleElement()
                .satisfies(reaction -> {
                    assertThat(reaction).isSameAs(createdReaction);
                    assertThat(reaction.getType()).isEqualTo(ReactionType.DISLIKE);
                });
    }

    @Test
    void shouldRemoveExistingReactionWhenUserRepeatsSameType() {
        TestFeedback feedback = feedback();
        User reactor = user(UUID.randomUUID(), "reactor@example.com");
        feedback.react(reactor, ReactionType.LIKE);

        boolean changed = feedback.react(reactor, ReactionType.LIKE);

        assertThat(changed).isTrue();
        assertThat(feedback.getReactions()).isEmpty();
    }

    @Test
    void shouldExposeImmutableReactionsList() {
        TestFeedback feedback = feedback();
        feedback.react(user(UUID.randomUUID(), "reactor@example.com"), ReactionType.LIKE);

        List<FeedbackReaction> reactions = feedback.getReactions();

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> reactions.add(new FeedbackReaction()));
    }

    @Test
    void shouldLoadMappedReactionsIgnoringNullItems() {
        TestFeedback feedback = feedback();
        User reactor = user(UUID.randomUUID(), "reactor@example.com");
        FeedbackReaction reaction = new FeedbackReaction();
        reaction.setUser(reactor);
        reaction.setType(ReactionType.DISLIKE);

        feedback.setReactions(Arrays.asList(null, reaction));

        assertThat(feedback.getReactions())
                .singleElement()
                .satisfies(loadedReaction -> {
                    assertThat(loadedReaction).isSameAs(reaction);
                    assertThat(loadedReaction.getFeedback()).isSameAs(feedback);
                    assertThat(loadedReaction.getType()).isEqualTo(ReactionType.DISLIKE);
                });
    }

    @Test
    void shouldRejectInvalidReactionInputs() {
        TestFeedback feedback = feedback();
        User userWithoutId = user(null, "without-id@example.com");

        assertThatThrownBy(() -> feedback.react(null, ReactionType.LIKE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> feedback.react(user(UUID.randomUUID(), "reactor@example.com"), null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> feedback.react(userWithoutId, ReactionType.LIKE))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldUpdateCommentVisibilityAndUpdatedAtOnlyWhenStateChanges() {
        TestFeedback feedback = feedback();
        LocalDateTime baseline = LocalDateTime.of(2026, 1, 1, 10, 0);
        feedback.setUpdatedAt(baseline);

        feedback.editComment("  updated comment  ");
        LocalDateTime afterCommentEdit = feedback.getUpdatedAt();

        assertThat(feedback.getComment()).isEqualTo("updated comment");
        assertThat(afterCommentEdit).isAfter(baseline);

        feedback.editComment("updated comment");
        assertThat(feedback.getUpdatedAt()).isEqualTo(afterCommentEdit);

        feedback.hide();
        LocalDateTime afterHide = feedback.getUpdatedAt();
        assertThat(feedback.isVisible()).isFalse();
        assertThat(afterHide).isAfterOrEqualTo(afterCommentEdit);

        feedback.hide();
        assertThat(feedback.getUpdatedAt()).isEqualTo(afterHide);

        feedback.show();
        assertThat(feedback.isVisible()).isTrue();
        assertThat(feedback.getUpdatedAt()).isAfterOrEqualTo(afterHide);
    }

    private static TestFeedback feedback() {
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
