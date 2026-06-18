package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentEligibilityTest {

    @Test
    void shouldRejectNullUser() {
        Assessment assessment = assessment();

        assertThat(assessment.canBeAttemptedBy(null)).isFalse();
    }

    @Test
    void shouldRejectInactiveUser() {
        Assessment assessment = assessment();
        UserProfessional user = activeProfessional();
        user.setStatus(UserStatus.INACTIVE);
        user.setRegistrationDate(LocalDateTime.now().minusMinutes(2));

        assertThat(assessment.canBeAttemptedBy(user)).isFalse();
    }

    @Test
    void shouldAllowActiveUserWithoutRegistrationDate() {
        Assessment assessment = assessment();
        UserProfessional user = activeProfessional();
        user.setRegistrationDate(null);

        assertThat(assessment.canBeAttemptedBy(user)).isTrue();
    }

    @Test
    void shouldAllowActiveUserRegisteredMoreThanOneMinuteAgo() {
        Assessment assessment = assessment();
        UserProfessional user = activeProfessional();
        user.setRegistrationDate(LocalDateTime.now().minusMinutes(2));

        assertThat(assessment.canBeAttemptedBy(user)).isTrue();
    }

    @Test
    void shouldRejectActiveUserRegisteredTooRecently() {
        Assessment assessment = assessment();
        UserProfessional user = activeProfessional();
        user.setRegistrationDate(LocalDateTime.now());

        assertThat(assessment.canBeAttemptedBy(user)).isFalse();
    }

    private Assessment assessment() {
        return new SystemAssessment(
                UUID.randomUUID(),
                "System assessment",
                "Description",
                LocalDateTime.now(),
                List.of(),
                Duration.ofHours(1)
        );
    }

    private UserProfessional activeProfessional() {
        UserProfessional user = new UserProfessional();
        user.setName("Candidate");
        user.setEmail("candidate@example.com");
        user.setPasswordHash("hashed");
        user.setExperienceLevel(ExperienceLevel.JUNIOR);
        user.setRole(UserRole.PROFESSIONAL);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
