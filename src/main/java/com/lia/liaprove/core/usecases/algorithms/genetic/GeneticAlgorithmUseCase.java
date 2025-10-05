package com.lia.liaprove.core.usecases.algorithms.genetic;

import com.lia.liaprove.core.algorithms.genetic.RecruiterMetrics;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Serviço de alto nível para executar o ajuste de pesos dos recrutadores.
 */
public interface GeneticAlgorithmUseCase {
    /**
     * Executa o algoritmo usando os dados obtidos pela camada application (gateway).
     * @return map de recruiterId -> novo voteWeight (0..10 por convenção)
     */
    Map<UUID, Integer> runAdjustVoteWeights();

    /**
     * Mesma operação, mas permitindo injetar os dados (útil para testes e dry-run locais,
     * sem necessidade de mockar gateway).
     * Implementação pode optar por persistir os valores ou apenas retornar o mapa.
     */
    Map<UUID, Integer> runAdjustVoteWeights(List<RecruiterMetrics> seedMetrics);
}
