package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssessmentAttemptVoteTest {

    @Test
    void shouldRequireConstructorArguments() {
        User user = user(UUID.randomUUID(), "voter@example.com");
        AssessmentAttempt attempt = assessmentAttempt(UUID.randomUUID(), user);

        assertThatThrownBy(() -> new AssessmentAttemptVote(null, attempt, VoteType.APPROVE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new AssessmentAttemptVote(user, null, VoteType.APPROVE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new AssessmentAttemptVote(user, attempt, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldInitializeTimestampsOnCreation() {
        AssessmentAttemptVote vote = vote();

        assertThat(vote.getCreatedAt()).isNotNull();
        assertThat(vote.getUpdatedAt()).isEqualTo(vote.getCreatedAt());
    }

    @Test
    void shouldPreventChangingIdentityFieldsAfterTheyAreDefined() {
        AssessmentAttemptVote vote = vote();
        vote.setId(UUID.randomUUID());
        LocalDateTime createdAt = vote.getCreatedAt();

        assertThatThrownBy(() -> vote.setId(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> vote.setUser(user(UUID.randomUUID(), "other@example.com")))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> vote.setAssessmentAttempt(assessmentAttempt(UUID.randomUUID(), vote.getUser())))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> vote.setCreatedAt(createdAt.plusDays(1)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldRejectNullVoteType() {
        AssessmentAttemptVote vote = vote();

        assertThatThrownBy(() -> vote.setVoteType(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldUpdateTimestampWhenVoteTypeChanges() {
        AssessmentAttemptVote vote = vote();
        LocalDateTime baseline = LocalDateTime.of(2026, 1, 1, 10, 0);
        vote.setUpdatedAt(baseline);

        vote.setVoteType(VoteType.REJECT);

        assertThat(vote.getVoteType()).isEqualTo(VoteType.REJECT);
        assertThat(vote.getUpdatedAt()).isAfter(baseline);
    }

    private static AssessmentAttemptVote vote() {
        User user = user(UUID.randomUUID(), "voter@example.com");
        return new AssessmentAttemptVote(
                user,
                assessmentAttempt(UUID.randomUUID(), user),
                VoteType.APPROVE
        );
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
