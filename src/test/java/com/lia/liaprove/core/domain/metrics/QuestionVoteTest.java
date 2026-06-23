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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestionVoteTest {

    @Test
    void shouldRequireQuestion() {
        User user = user(UUID.randomUUID(), "voter@example.com");

        assertThatThrownBy(() -> new QuestionVote(user, null, VoteType.APPROVE))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldInitializeQuestionAndInheritVoteBehavior() {
        QuestionVote questionVote = vote();

        assertThat(questionVote).isInstanceOf(Vote.class);
        assertThat(questionVote.getQuestion()).isNotNull();
        assertThat(questionVote.getUser()).isNotNull();
        assertThat(questionVote.getVoteType()).isEqualTo(VoteType.APPROVE);
    }

    @Test
    void shouldPreventChangingQuestionAfterItIsDefined() {
        QuestionVote questionVote = vote();

        assertThatThrownBy(() -> questionVote.setQuestion(question(UUID.randomUUID())))
                .isInstanceOf(IllegalStateException.class);
    }

    private static QuestionVote vote() {
        return new QuestionVote(
                user(UUID.randomUUID(), "voter@example.com"),
                question(UUID.randomUUID()),
                VoteType.APPROVE
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
