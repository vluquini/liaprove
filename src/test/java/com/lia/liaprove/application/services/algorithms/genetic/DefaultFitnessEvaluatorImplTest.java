package com.lia.liaprove.application.services.algorithms.genetic;

import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.algorithms.genetic.Individual;
import com.lia.liaprove.core.algorithms.genetic.RecruiterMetrics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFitnessEvaluatorImplTest {

    private final GeneticConfig config = GeneticConfig.defaults();
    private final DefaultFitnessEvaluatorImpl evaluator = DefaultFitnessEvaluatorImpl.defaultEvaluator(config);

    @Test
    @DisplayName("Should reward candidate gene closer to recruiter target score")
    void shouldRewardGeneCloserToTargetScore() {
        RecruiterMetrics metrics = strongRecruiterMetrics();
        Individual closeCandidate = new Individual(metrics.getRecruiterId(), 0.95);
        Individual farCandidate = new Individual(metrics.getRecruiterId(), 0.10);

        double closeFitness = evaluator.evaluate(closeCandidate, metrics);
        double farFitness = evaluator.evaluate(farCandidate, metrics);

        assertThat(closeFitness).isGreaterThan(farFitness);
        assertThat(closeFitness).isBetween(0.0, 1.0);
    }

    @Test
    @DisplayName("Should keep new recruiter neutral instead of punitive")
    void shouldKeepNewRecruiterNeutral() {
        RecruiterMetrics metrics = new RecruiterMetrics(
                UUID.randomUUID(), 1, 0, 0, 0.0, 0, 0, 0, Double.NaN, 2.5
        );

        double neutralFitness = evaluator.evaluate(new Individual(metrics.getRecruiterId(), 0.5), metrics);
        double lowFitness = evaluator.evaluate(new Individual(metrics.getRecruiterId(), 0.0), metrics);

        assertThat(neutralFitness).isGreaterThan(lowFitness);
    }

    @Test
    @DisplayName("Should reduce target when recruiter reputation is low")
    void shouldReduceTargetWhenReputationIsLow() {
        UUID recruiterId = UUID.randomUUID();
        RecruiterMetrics highReputation = new RecruiterMetrics(
                recruiterId, 5, 100, 10, 4.5, 20, 10, 0, 1.0, 5.0
        );
        RecruiterMetrics lowReputation = new RecruiterMetrics(
                recruiterId, 5, 100, 10, 4.5, 20, 0, 10, 0.0, 0.0
        );

        double highFitnessForHighGene = evaluator.evaluate(new Individual(recruiterId, 0.9), highReputation);
        double lowFitnessForHighGene = evaluator.evaluate(new Individual(recruiterId, 0.9), lowReputation);

        assertThat(highFitnessForHighGene).isGreaterThan(lowFitnessForHighGene);
    }

    private RecruiterMetrics strongRecruiterMetrics() {
        return new RecruiterMetrics(
                UUID.randomUUID(),
                8,
                100,
                10,
                4.8,
                20,
                30,
                2,
                0.94,
                5.0
        );
    }
}
