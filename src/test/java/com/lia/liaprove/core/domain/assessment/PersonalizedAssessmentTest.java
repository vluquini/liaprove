package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.Question;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonalizedAssessmentTest {

    @Test
    void shouldInitializeTotalAttemptsWithZero() {
        PersonalizedAssessment assessment = assessment(3);

        assertThat(assessment.getTotalAttempts()).isZero();
    }

    @Test
    void shouldDefaultCriteriaWeightsWhenNull() {
        PersonalizedAssessment assessment = new PersonalizedAssessment(
                UUID.randomUUID(),
                "Assessment",
                "Description",
                LocalDateTime.now(),
                List.of(),
                Duration.ofMinutes(30),
                null,
                LocalDateTime.now().plusDays(1),
                3,
                "share-token",
                PersonalizedAssessmentStatus.ACTIVE,
                null
        );

        assertThat(assessment.getCriteriaWeights()).isEqualTo(AssessmentCriteriaWeights.defaultWeights());
    }

    @Test
    void shouldRejectInvalidMaxAttempts() {
        assertThatThrownBy(() -> assessment(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("maxAttempts must be greater than zero");
    }

    @Test
    void shouldRejectNegativeTotalAttemptsWhenLoadedFromPersistence() {
        PersonalizedAssessment assessment = assessment(3);

        assertThatThrownBy(() -> assessment.setTotalAttempts(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("totalAttempts must not be negative");
    }

    @Test
    void shouldIdentifyExpiredAssessment() {
        PersonalizedAssessment assessment = assessment(3);
        LocalDateTime reference = LocalDateTime.now();
        assessment.setExpirationDate(reference.minusMinutes(1));

        assertThat(assessment.isExpired(reference)).isTrue();
    }

    @Test
    void shouldNotExpireWithoutExpirationOrReferenceDate() {
        PersonalizedAssessment assessment = assessment(3);

        assessment.setExpirationDate(null);
        assertThat(assessment.isExpired(LocalDateTime.now())).isFalse();

        assessment.setExpirationDate(LocalDateTime.now().minusMinutes(1));
        assertThat(assessment.isExpired(null)).isFalse();
    }

    @Test
    void shouldRejectNullStatus() {
        assertThatThrownBy(() -> new PersonalizedAssessment(
                UUID.randomUUID(),
                "Assessment",
                "Description",
                LocalDateTime.now(),
                List.of(),
                Duration.ofMinutes(30),
                null,
                LocalDateTime.now().plusDays(1),
                3,
                "share-token",
                null,
                AssessmentCriteriaWeights.defaultWeights()
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("status must not be null");
    }

    @Test
    void shouldIdentifyReachedMaxAttempts() {
        PersonalizedAssessment assessment = assessment(3);

        assertThat(assessment.hasReachedMaxAttempts(3)).isTrue();
        assertThat(assessment.hasReachedMaxAttempts(2)).isFalse();
    }

    @Test
    void shouldChangeStatusThroughExplicitMethods() {
        PersonalizedAssessment assessment = assessment(3);

        assessment.deactivate();
        assertThat(assessment.getStatus()).isEqualTo(PersonalizedAssessmentStatus.DEACTIVATED);

        assessment.activate();
        assertThat(assessment.getStatus()).isEqualTo(PersonalizedAssessmentStatus.ACTIVE);

        assessment.revoke();
        assertThat(assessment.getStatus()).isEqualTo(PersonalizedAssessmentStatus.REVOKED);

        assessment.close();
        assertThat(assessment.getStatus()).isEqualTo(PersonalizedAssessmentStatus.CLOSED);
    }

    private PersonalizedAssessment assessment(int maxAttempts) {
        return new PersonalizedAssessment(
                UUID.randomUUID(),
                "Assessment",
                "Description",
                LocalDateTime.now(),
                List.of(),
                Duration.ofMinutes(30),
                null,
                LocalDateTime.now().plusDays(1),
                maxAttempts,
                "share-token",
                PersonalizedAssessmentStatus.ACTIVE,
                AssessmentCriteriaWeights.defaultWeights()
        );
    }
}
