package com.lia.liaprove.core.usecases.algorithms.genetic;

import java.util.Map;
import java.util.UUID;

/**
 * Serviço de alto nível para executar o ajuste de pesos dos recrutadores.
 */
public interface GeneticAlgorithmUseCase {
    /**
     * Executa o processo de ajuste de pesos em batch e persiste/retorna o novo mapa id->weight.
     * Normalmente será chamado por agendador ou manualmente.
     */
    Map<UUID, Integer> runAdjustVoteWeights();
}
