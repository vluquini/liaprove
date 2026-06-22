package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackQuestionTest {

    @Test
    void shouldInitializeQuestionFeedbackSpecificData() {
        User author = user(UUID.randomUUID(), "author@example.com");
        Question question = question(UUID.randomUUID());
        LocalDateTime submittedAt = LocalDateTime.of(2026, 1, 2, 10, 0);

        FeedbackQuestion feedback = new FeedbackQuestion(
                author,
                "Useful question",
                submittedAt,
                question,
                DifficultyLevel.MEDIUM,
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                RelevanceLevel.FOUR
        );

        assertThat(feedback.getUser()).isEqualTo(author);
        assertThat(feedback.getComment()).isEqualTo("Useful question");
        assertThat(feedback.getSubmissionDate()).isEqualTo(submittedAt);
        assertThat(feedback.isVisible()).isTrue();
        assertThat(feedback.getQuestion()).isEqualTo(question);
        assertThat(feedback.getDifficultyLevel()).isEqualTo(DifficultyLevel.MEDIUM);
        assertThat(feedback.getKnowledgeArea()).isEqualTo(KnowledgeArea.SOFTWARE_DEVELOPMENT);
        assertThat(feedback.getRelevanceLevel()).isEqualTo(RelevanceLevel.FOUR);
    }

    @Test
    void shouldPreventChangingQuestionMetadataAfterItIsDefined() {
        FeedbackQuestion feedback = feedbackQuestion();

        assertThatThrownBy(() -> feedback.setQuestion(question(UUID.randomUUID())))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> feedback.setDifficultyLevel(DifficultyLevel.HARD))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> feedback.setKnowledgeArea(KnowledgeArea.DATABASE))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> feedback.setRelevanceLevel(RelevanceLevel.ONE))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldUseFeedbackReactionApiWithoutDeclaringQuestionSpecificReactionMethods() {
        FeedbackQuestion feedback = feedbackQuestion();
        User reactor = user(UUID.randomUUID(), "reactor@example.com");

        feedback.react(reactor, ReactionType.LIKE);

        assertThat(feedback.getReactions())
                .singleElement()
                .satisfies(reaction -> {
                    assertThat(reaction.getFeedback()).isSameAs(feedback);
                    assertThat(reaction.getUser()).isEqualTo(reactor);
                    assertThat(reaction.getType()).isEqualTo(ReactionType.LIKE);
                });
        assertThat(declaredMethodNames())
                .doesNotContain(
                        "setReactionsByUser",
                        "manageReaction",
                        "removeReaction",
                        "countLikes",
                        "countDislikes",
                        "netLikes",
                        "userHasLiked",
                        "getReactionTypeByUser",
                        "likeRatio",
                        "getReactions",
                        "setReactions"
                );
    }

    private static List<String> declaredMethodNames() {
        return Arrays.stream(FeedbackQuestion.class.getDeclaredMethods())
                .map(Method::getName)
                .toList();
    }

    private static FeedbackQuestion feedbackQuestion() {
        return new FeedbackQuestion(
                user(UUID.randomUUID(), "author@example.com"),
                "Useful question",
                LocalDateTime.of(2026, 1, 2, 10, 0),
                question(UUID.randomUUID()),
                DifficultyLevel.MEDIUM,
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                RelevanceLevel.FOUR
        );
    }

    private static Question question(UUID id) {
        return new ProjectQuestion(
                id,
                UUID.randomUUID(),
                "Project question",
                "Build something useful",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FOUR,
                LocalDateTime.of(2026, 1, 1, 9, 0),
                LocalDateTime.of(2026, 1, 8, 9, 0),
                QuestionStatus.VOTING,
                RelevanceLevel.THREE,
                0
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
