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

class VoteTest {

    @Test
    void shouldRequireUserAndVoteType() {
        User user = user(UUID.randomUUID(), "voter@example.com");

        assertThatThrownBy(() -> new TestVote(null, VoteType.APPROVE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new TestVote(user, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldInitializeTimestampsOnCreation() {
        TestVote vote = new TestVote(user(UUID.randomUUID(), "voter@example.com"), VoteType.APPROVE);

        assertThat(vote.getCreatedAt()).isNotNull();
        assertThat(vote.getUpdatedAt()).isEqualTo(vote.getCreatedAt());
    }

    @Test
    void shouldPreventChangingCommonIdentityFieldsAfterTheyAreDefined() {
        TestVote vote = new TestVote(user(UUID.randomUUID(), "voter@example.com"), VoteType.APPROVE);
        vote.setId(UUID.randomUUID());
        LocalDateTime createdAt = vote.getCreatedAt();

        assertThatThrownBy(() -> vote.setId(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> vote.setUser(user(UUID.randomUUID(), "other@example.com")))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> vote.setCreatedAt(createdAt.plusDays(1)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldRejectNullVoteType() {
        TestVote vote = new TestVote(user(UUID.randomUUID(), "voter@example.com"), VoteType.APPROVE);

        assertThatThrownBy(() -> vote.setVoteType(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldUpdateTimestampWhenVoteTypeChanges() {
        TestVote vote = new TestVote(user(UUID.randomUUID(), "voter@example.com"), VoteType.APPROVE);
        LocalDateTime baseline = LocalDateTime.of(2026, 1, 1, 10, 0);
        vote.setUpdatedAt(baseline);

        vote.setVoteType(VoteType.REJECT);

        assertThat(vote.getVoteType()).isEqualTo(VoteType.REJECT);
        assertThat(vote.getUpdatedAt()).isAfter(baseline);
    }

    @Test
    void shouldNotUpdateTimestampWhenVoteTypeDoesNotChange() {
        TestVote vote = new TestVote(user(UUID.randomUUID(), "voter@example.com"), VoteType.APPROVE);
        LocalDateTime baseline = LocalDateTime.of(2026, 1, 1, 10, 0);
        vote.setUpdatedAt(baseline);

        vote.setVoteType(VoteType.APPROVE);

        assertThat(vote.getUpdatedAt()).isEqualTo(baseline);
    }

    @Test
    void shouldCompareByIdSafelyWhenIdsAreNull() {
        TestVote first = new TestVote();
        TestVote second = new TestVote();

        assertThatCode(() -> first.equals(second)).doesNotThrowAnyException();
    }

    private static final class TestVote extends Vote {
        private TestVote() {
        }

        private TestVote(User user, VoteType voteType) {
            super(user, voteType);
        }
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
