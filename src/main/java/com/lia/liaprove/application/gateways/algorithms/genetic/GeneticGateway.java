package com.lia.liaprove.application.gateways.algorithms.genetic;

import com.lia.liaprove.core.algorithms.genetic.RecruiterMetrics;

import java.util.List;

/**
 * Gateway/port: infra deve gerar um objeto RecruiterMetrics semanalmente para cálculo do GA.
 * - fetchAllRecruiterMetrics: retorna métricas agregadas (janela semanal etc).
 */
public interface GeneticGateway {

    /**
     * Retorna métricas agregadas para todos os recruiters relevantes.
     * Implementação pode paginar, filtrar por ativos, etc.
     */
    List<RecruiterMetrics> fetchAllRecruiterMetrics();
}
