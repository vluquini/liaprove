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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestionVoteTest {

    @Test
    void shouldRequireConstructorArguments() {
        User user = user(UUID.randomUUID(), "voter@example.com");
        Question question = question(UUID.randomUUID());

        assertThatThrownBy(() -> new QuestionVote(null, question, VoteType.APPROVE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new QuestionVote(user, null, VoteType.APPROVE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new QuestionVote(user, question, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldInitializeTimestampsOnCreation() {
        QuestionVote questionVote = vote();

        assertThat(questionVote.getCreatedAt()).isNotNull();
        assertThat(questionVote.getUpdatedAt()).isEqualTo(questionVote.getCreatedAt());
    }

    @Test
    void shouldPreventChangingIdentityFieldsAfterTheyAreDefined() {
        QuestionVote questionVote = vote();
        questionVote.setId(UUID.randomUUID());
        LocalDateTime createdAt = questionVote.getCreatedAt();

        assertThatThrownBy(() -> questionVote.setId(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> questionVote.setUser(user(UUID.randomUUID(), "other@example.com")))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> questionVote.setQuestion(question(UUID.randomUUID())))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> questionVote.setCreatedAt(createdAt.plusDays(1)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldRejectNullVoteType() {
        QuestionVote questionVote = vote();

        assertThatThrownBy(() -> questionVote.setVoteType(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldUpdateTimestampWhenVoteTypeChanges() {
        QuestionVote questionVote = vote();
        LocalDateTime baseline = LocalDateTime.of(2026, 1, 1, 10, 0);
        questionVote.setUpdatedAt(baseline);

        questionVote.setVoteType(VoteType.REJECT);

        assertThat(questionVote.getVoteType()).isEqualTo(VoteType.REJECT);
        assertThat(questionVote.getUpdatedAt()).isAfter(baseline);
    }

    @Test
    void shouldCompareByIdSafelyWhenIdsAreNull() {
        QuestionVote first = new QuestionVote();
        QuestionVote second = new QuestionVote();

        assertThatCode(() -> first.equals(second)).doesNotThrowAnyException();
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
