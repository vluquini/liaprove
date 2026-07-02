package com.lia.liaprove.infrastructure.services.algorithms.genetic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeneticGatewayImplTest {

    @Test
    @DisplayName("Should derive neutral recruiter rating when feedback has no reactions")
    void shouldDeriveNeutralRatingWithoutReactions() {
        assertThat(GeneticGatewayImpl.toSmoothedRecruiterRating(0, 0)).isEqualTo(2.5);
    }

    @Test
    @DisplayName("Should derive lower recruiter rating when dislikes dominate")
    void shouldDeriveLowerRatingWhenDislikesDominate() {
        double rating = GeneticGatewayImpl.toSmoothedRecruiterRating(0, 8);

        assertThat(rating).isLessThan(2.5);
        assertThat(rating).isGreaterThanOrEqualTo(0.0);
    }
}
