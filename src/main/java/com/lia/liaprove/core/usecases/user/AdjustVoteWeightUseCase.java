package com.lia.liaprove.core.usecases.user;

/**
 * Entrada para o mecanismo que ajusta pesos de voto (ex.: executado por um agendador
 * ou manualmente por administrador). Implementação delega ao serviço de algoritmos.
 */
public interface AdjustVoteWeightUseCase {
    void adjustAllRecruiterWeights();
}