package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Certificate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void shouldCreateUserWithActiveStatusFromFullConstructor() {
        User user = new TestUser(
                UUID.randomUUID(),
                "Ana",
                "ana@example.com",
                "hashed",
                "Developer",
                "Bio",
                ExperienceLevel.PLENO,
                UserRole.PROFESSIONAL,
                1,
                0,
                List.of(),
                0f,
                LocalDateTime.now(),
                null
        );

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void shouldRejectBlankPasswordHash() {
        User user = new TestUser();

        assertThatThrownBy(() -> user.setPasswordHash(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("hashedPassword must not be null/blank");
    }

    @Test
    void shouldRecordAssessmentResultUsingIncrementalAverage() {
        User user = new TestUser();
        user.setTotalAssessmentsTaken(2);
        user.setAverageScore(80f);

        user.recordAssessmentResult(50f);

        assertThat(user.getTotalAssessmentsTaken()).isEqualTo(3);
        assertThat(user.getAverageScore()).isEqualTo(70f);
    }

    @Test
    void shouldRejectNegativeAssessmentScore() {
        User user = new TestUser();

        assertThatThrownBy(() -> user.recordAssessmentResult(-1f))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score must be >= 0");
    }

    @Test
    void shouldUpdateExperienceLevelUsingSetterAndRejectNull() {
        User user = new TestUser();
        user.setExperienceLevel(ExperienceLevel.JUNIOR);

        user.setExperienceLevel(ExperienceLevel.SENIOR);

        assertThat(user.getExperienceLevel()).isEqualTo(ExperienceLevel.SENIOR);
        assertThatThrownBy(() -> user.setExperienceLevel(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("experienceLevel must not be null");
    }

    @Test
    void shouldSetVoteWeightWithinProvidedLimits() {
        User user = new TestUser();

        user.setVoteWeightSafely(3, 1, 5);

        assertThat(user.getVoteWeight()).isEqualTo(3);
    }

    @Test
    void shouldRejectVoteWeightOutsideLimitsOrInvalidLimitRange() {
        User user = new TestUser();

        assertThatThrownBy(() -> user.setVoteWeightSafely(0, 1, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("voteWeight must be between 1 and 5");
        assertThatThrownBy(() -> user.setVoteWeightSafely(6, 1, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("voteWeight must be between 1 and 5");
        assertThatThrownBy(() -> user.setVoteWeightSafely(3, 5, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("min must be <= max");
    }

    @Test
    void shouldAdjustVoteWeightClampingToLimits() {
        User user = new TestUser();
        user.setVoteWeight(4);

        user.adjustVoteWeight(10, 1, 5);
        assertThat(user.getVoteWeight()).isEqualTo(5);

        user.adjustVoteWeight(-10, 1, 5);
        assertThat(user.getVoteWeight()).isEqualTo(1);
    }

    @Test
    void shouldAdjustVoteWeightFromMinimumWhenCurrentWeightIsNull() {
        User user = new TestUser();

        user.adjustVoteWeight(2, 1, 5);

        assertThat(user.getVoteWeight()).isEqualTo(3);
        assertThatThrownBy(() -> user.adjustVoteWeight(1, 5, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("min must be <= max");
    }

    @Test
    void shouldUpdateProfileTrimmingValuesAndClearingOptionalTextFields() {
        User user = new TestUser();
        user.setName("Original");
        user.setEmail("original@example.com");
        user.setOccupation("Developer");
        user.setBio("Original bio");
        user.setExperienceLevel(ExperienceLevel.JUNIOR);

        user.updateProfile("  Maria  ", " maria@example.com ", " ", "   ", ExperienceLevel.PLENO);

        assertThat(user.getName()).isEqualTo("Maria");
        assertThat(user.getEmail()).isEqualTo("maria@example.com");
        assertThat(user.getOccupation()).isEmpty();
        assertThat(user.getBio()).isEmpty();
        assertThat(user.getExperienceLevel()).isEqualTo(ExperienceLevel.PLENO);
    }

    @Test
    void shouldKeepNameAndEmailWhenProfileReceivesBlankValues() {
        User user = new TestUser();
        user.setName("Original");
        user.setEmail("original@example.com");

        user.updateProfile(" ", " ", "  Developer  ", "  Bio  ", null);

        assertThat(user.getName()).isEqualTo("Original");
        assertThat(user.getEmail()).isEqualTo("original@example.com");
        assertThat(user.getOccupation()).isEqualTo("Developer");
        assertThat(user.getBio()).isEqualTo("Bio");
    }

    @Test
    void shouldIgnoreNullProfileFields() {
        User user = new TestUser();
        user.setName("Original");
        user.setEmail("original@example.com");
        user.setOccupation("Developer");
        user.setBio("Original bio");
        user.setExperienceLevel(ExperienceLevel.JUNIOR);

        user.updateProfile(null, null, null, null, null);

        assertThat(user.getName()).isEqualTo("Original");
        assertThat(user.getEmail()).isEqualTo("original@example.com");
        assertThat(user.getOccupation()).isEqualTo("Developer");
        assertThat(user.getBio()).isEqualTo("Original bio");
        assertThat(user.getExperienceLevel()).isEqualTo(ExperienceLevel.JUNIOR);
    }

    private static final class TestUser extends User {
        private TestUser() {
        }

        private TestUser(UUID id, String name, String email, String passwordHash, String occupation, String bio,
                         ExperienceLevel experienceLevel, UserRole role, Integer voteWeight,
                         Integer totalAssessmentsTaken, List<Certificate> certificates, Float averageScore,
                         LocalDateTime registrationDate, LocalDateTime lastLogin) {
            super(id, name, email, passwordHash, occupation, bio, experienceLevel, role, voteWeight,
                    totalAssessmentsTaken, certificates, averageScore, registrationDate, lastLogin);
        }
    }
}
