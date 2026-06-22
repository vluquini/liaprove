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

class VoteTest {

    @Test
    void shouldRequireConstructorArguments() {
        User user = user(UUID.randomUUID(), "voter@example.com");
        Question question = question(UUID.randomUUID());

        assertThatThrownBy(() -> new Vote(null, question, VoteType.APPROVE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Vote(user, null, VoteType.APPROVE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Vote(user, question, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldInitializeTimestampsOnCreation() {
        Vote vote = vote();

        assertThat(vote.getCreatedAt()).isNotNull();
        assertThat(vote.getUpdatedAt()).isEqualTo(vote.getCreatedAt());
    }

    @Test
    void shouldPreventChangingIdentityFieldsAfterTheyAreDefined() {
        Vote vote = vote();
        vote.setId(UUID.randomUUID());
        LocalDateTime createdAt = vote.getCreatedAt();

        assertThatThrownBy(() -> vote.setId(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> vote.setUser(user(UUID.randomUUID(), "other@example.com")))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> vote.setQuestion(question(UUID.randomUUID())))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> vote.setCreatedAt(createdAt.plusDays(1)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldRejectNullVoteType() {
        Vote vote = vote();

        assertThatThrownBy(() -> vote.setVoteType(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldUpdateTimestampWhenVoteTypeChanges() {
        Vote vote = vote();
        LocalDateTime baseline = LocalDateTime.of(2026, 1, 1, 10, 0);
        vote.setUpdatedAt(baseline);

        vote.setVoteType(VoteType.REJECT);

        assertThat(vote.getVoteType()).isEqualTo(VoteType.REJECT);
        assertThat(vote.getUpdatedAt()).isAfter(baseline);
    }

    @Test
    void shouldCompareByIdSafelyWhenIdsAreNull() {
        Vote first = new Vote();
        Vote second = new Vote();

        assertThatCode(() -> first.equals(second)).doesNotThrowAnyException();
    }

    private static Vote vote() {
        return new Vote(
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
