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
    void shouldRequireAssessmentAttempt() {
        User user = user(UUID.randomUUID(), "voter@example.com");

        assertThatThrownBy(() -> new AssessmentAttemptVote(user, null, VoteType.APPROVE))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldInitializeAssessmentAttemptAndInheritVoteBehavior() {
        AssessmentAttemptVote vote = vote();

        assertThat(vote).isInstanceOf(Vote.class);
        assertThat(vote.getAssessmentAttempt()).isNotNull();
        assertThat(vote.getUser()).isNotNull();
        assertThat(vote.getVoteType()).isEqualTo(VoteType.APPROVE);
    }

    @Test
    void shouldPreventChangingAssessmentAttemptAfterItIsDefined() {
        AssessmentAttemptVote vote = vote();

        assertThatThrownBy(() -> vote.setAssessmentAttempt(assessmentAttempt(UUID.randomUUID(), vote.getUser())))
                .isInstanceOf(IllegalStateException.class);
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
