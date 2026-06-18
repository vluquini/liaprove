package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.SystemAssessment;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserProfessionalTest {

    @Test
    void shouldNormalizeSkillsByTrimmingLowercasingRemovingBlankValuesAndDuplicates() {
        UserProfessional professional = new UserProfessional();

        professional.setHardSkills(List.of(" Java ", "java", " ", "Spring", "Spring"));
        professional.setSoftSkills(List.of(" Communication ", "", "Communication"));

        assertThat(professional.getHardSkills()).containsExactly("java", "spring");
        assertThat(professional.getSoftSkills()).containsExactly("communication");
    }

    @Test
    void shouldNormalizeNullSkillListsToEmptyLists() {
        UserProfessional professional = new UserProfessional();

        professional.setHardSkills(null);
        professional.setSoftSkills(null);

        assertThat(professional.getHardSkills()).isEmpty();
        assertThat(professional.getSoftSkills()).isEmpty();
    }

    @Test
    void shouldReplaceOnlyProvidedSkillLists() {
        UserProfessional professional = new UserProfessional();
        professional.setHardSkills(List.of("Java"));
        professional.setSoftSkills(List.of("Communication"));

        professional.updateSkills(null, List.of(" Leadership ", "Leadership"));

        assertThat(professional.getHardSkills()).containsExactly("java");
        assertThat(professional.getSoftSkills()).containsExactly("leadership");
    }

    @Test
    void shouldRecordAssessmentResultAsProfessional() {
        UserProfessional professional = new UserProfessional();
        professional.setTotalAssessmentsTaken(1);
        professional.setAverageScore(60f);

        professional.recordAssessmentResultAsProfessional(90f);

        assertThat(professional.getTotalAssessmentsTaken()).isEqualTo(2);
        assertThat(professional.getAverageScore()).isEqualTo(75f);
    }

    @Test
    void shouldRejectNegativeAssessmentResultAsProfessional() {
        UserProfessional professional = new UserProfessional();

        assertThatThrownBy(() -> professional.recordAssessmentResultAsProfessional(-0.1f))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score must be >= 0");
    }

    @Test
    void shouldUpdateProfessionalProfileClearingBlankOccupationAndBio() {
        UserProfessional professional = new UserProfessional();
        professional.setOccupation("Developer");
        professional.setBio("Original bio");

        professional.updateProfile(" ", "   ");

        assertThat(professional.getOccupation()).isEmpty();
        assertThat(professional.getBio()).isEmpty();
    }

    @Test
    void shouldDetermineAssessmentEligibilityUsingAssessmentPolicy() {
        Assessment assessment = assessment();
        UserProfessional professional = new UserProfessional();

        assertThat(professional.isEligibleForAssessment(null)).isFalse();

        professional.setStatus(UserStatus.ACTIVE);
        professional.setRegistrationDate(null);
        assertThat(professional.isEligibleForAssessment(assessment)).isTrue();

        professional.setRegistrationDate(LocalDateTime.now().minusMinutes(2));
        assertThat(professional.isEligibleForAssessment(assessment)).isTrue();

        professional.setRegistrationDate(LocalDateTime.now());
        assertThat(professional.isEligibleForAssessment(assessment)).isFalse();

        professional.setStatus(UserStatus.INACTIVE);
        professional.setRegistrationDate(LocalDateTime.now().minusMinutes(2));
        assertThat(professional.isEligibleForAssessment(assessment)).isFalse();
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
}
