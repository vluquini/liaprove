package com.lia.liaprove.core.usecases.user.admin;

import java.util.Map;
import java.util.UUID;

/**
 * Permitir disparo manual dos algoritmos (GA/BN) durante testes.
 *
 * <p>As implementações podem optar por persistir resultados ou apenas retorná-los para análise.
 */
public interface TriggerAlgorithmsUseCase {
    /**
     * Dispara o Algoritmo Genético para recalcular os {@code voteWeight} dos recruiters.
     *
     * @param  adminId id do administrador (ou sistema) que disparou a execução
     * @return mapa contendo {@code recruiterId} -> {@code novoVoteWeight} (escala conforme GeneticConfig)
     */
    Map<UUID,Integer> triggerGeneticAlgorithm(UUID adminId);

    /**
     * Executa a avaliação bayesiana para todas as questões candidatas e retorna os scores.
     *
     * @param  adminId id do administrador (ou sistema) que disparou a execução
     * @return mapa contendo {@code questionId} -> {@code score}, onde score está normalizado entre 0.0 e 1.0
     */
    Map<UUID, Double> triggerBayesianEvaluationForAll(UUID adminId);

    /**
     * Executa a avaliação bayesiana para uma única questão e retorna seu score.
     *
     * @param  questionId id da questão a ser avaliada
     * @param  adminId id do administrador (ou sistema) que disparou a execução
     * @return score da questão (valor normalizado entre 0.0 e 1.0)
     */
    double triggerBayesianEvaluation(UUID questionId, UUID adminId);
}

