package com.lia.liaprove.application.services.algorithms.genetic;

import com.lia.liaprove.application.gateways.algorithms.genetic.GeneticGateway;
import com.lia.liaprove.core.algorithms.genetic.FitnessEvaluator;
import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.algorithms.genetic.RecruiterMetrics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GeneticAlgorithmUseCaseImplTest {

    @Test
    @DisplayName("Should preserve all recruiters even when configured population is smaller")
    void shouldPreserveAllRecruitersWhenPopulationIsSmaller() {
        GeneticConfig config = new GeneticConfig(1, 3, 0.0, 0.0, 1, 10, 10, 100, 20);
        GeneticGateway gateway = mock(GeneticGateway.class);
        FitnessEvaluator evaluator = DefaultFitnessEvaluatorImpl.defaultEvaluator(config);
        GeneticAlgorithmUseCaseImpl useCase = new GeneticAlgorithmUseCaseImpl(config, evaluator, gateway, new Random(1));

        List<RecruiterMetrics> metrics = List.of(metrics(1), metrics(2), metrics(3));

        Map<UUID, Integer> result = useCase.runAdjustVoteWeights(metrics);

        assertThat(result).containsOnlyKeys(
                metrics.get(0).getRecruiterId(),
                metrics.get(1).getRecruiterId(),
                metrics.get(2).getRecruiterId()
        );
        assertThat(result.values()).allSatisfy(weight -> assertThat(weight).isBetween(1, 10));
    }

    @Test
    @DisplayName("Should fetch metrics from gateway when no seed is provided")
    void shouldFetchMetricsFromGateway() {
        GeneticConfig config = GeneticConfig.defaults();
        GeneticGateway gateway = mock(GeneticGateway.class);
        List<RecruiterMetrics> metrics = List.of(metrics(5));
        when(gateway.fetchAllRecruiterMetrics()).thenReturn(metrics);

        GeneticAlgorithmUseCaseImpl useCase = new GeneticAlgorithmUseCaseImpl(
                config,
                DefaultFitnessEvaluatorImpl.defaultEvaluator(config),
                gateway,
                new Random(1)
        );

        Map<UUID, Integer> result = useCase.runAdjustVoteWeights();

        assertThat(result).containsKey(metrics.getFirst().getRecruiterId());
        verify(gateway).fetchAllRecruiterMetrics();
    }

    @Test
    @DisplayName("Should keep the first metrics entry when recruiter metrics are duplicated")
    void shouldKeepFirstMetricsEntryWhenRecruiterMetricsAreDuplicated() {
        GeneticConfig config = GeneticConfig.defaults();
        GeneticGateway gateway = mock(GeneticGateway.class);
        GeneticAlgorithmUseCaseImpl useCase = new GeneticAlgorithmUseCaseImpl(
                config,
                DefaultFitnessEvaluatorImpl.defaultEvaluator(config),
                gateway,
                new Random(1)
        );
        UUID recruiterId = UUID.randomUUID();
        RecruiterMetrics first = metrics(recruiterId, 2);
        RecruiterMetrics duplicate = metrics(recruiterId, 8);

        Map<UUID, Integer> result = useCase.runAdjustVoteWeights(List.of(first, duplicate));

        assertThat(result).containsOnlyKeys(recruiterId);
        assertThat(result.get(recruiterId)).isBetween(1, 10);
    }

    private RecruiterMetrics metrics(int currentWeight) {
        return metrics(UUID.randomUUID(), currentWeight);
    }

    private RecruiterMetrics metrics(UUID recruiterId, int currentWeight) {
        return new RecruiterMetrics(
                recruiterId, currentWeight, 20, 3, 4.0, 4, 5, 1, 0.83, 4.0
        );
    }
}
