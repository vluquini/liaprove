package com.lia.liaprove.application.gateways.algorithms.genetic;

import com.lia.liaprove.core.algorithms.genetic.RecruiterMetrics;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Gateway/port: infra deve gerar um objeto RecruiterMetrics semanalmente para cálculo do GA.
 * - fetchAllRecruiterMetrics: retorna métricas agregadas (janela semanal etc).
 * - updateVoteWeight / updateVoteWeightBulk: persiste novos pesos (0..10).
 */
public interface GeneticGateway {

    /**
     * Retorna métricas agregadas para todos os recruiters relevantes.
     * Implementação pode paginar, filtrar por ativos, etc.
     */
    List<RecruiterMetrics> fetchAllRecruiterMetrics();

    /**
     * Retorna métricas de um recruiter específico (opcional).
     */
    RecruiterMetrics fetchRecruiterMetrics(UUID recruiterId);

    /**
     * Persiste novo voteWeight (single).
     * Implementação deve validar range/clamp se necessário.
     */
    void updateVoteWeight(UUID recruiterId, int newWeight);

    /**
     * Atualiza em batch; comportamento atômico/transacional fica a cargo da infra.
     */
    default void updateVoteWeightBulk(Map<UUID, Integer> newWeights) {
        newWeights.forEach(this::updateVoteWeight);
    }
}