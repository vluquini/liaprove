package com.lia.liaprove.core.algorithms.genetic;

/**
 * Parâmetros configuráveis do GA.
 */
public final class GeneticConfig {
    private final int populationSize;
    private final int generations;
    private final double mutationRate;    // ex.: 0.05
    private final double crossoverRate;   // ex.: 0.7
    private final int minWeight;          // ex.: 0
    private final int maxWeight;          // ex.: 100

    // normalizadores / limites para features usadas no fitness
    private final int maxRecentAssessments;   // ex: 50 (para janela semanal/mensal)
    private final int maxQuestionsApproved;   // ex: 100

    public GeneticConfig(int populationSize, int generations, double mutationRate, double crossoverRate, int minWeight, int maxWeight, int maxRecentAssessments, int maxQuestionsApproved) {
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.maxRecentAssessments = maxRecentAssessments;
        this.maxQuestionsApproved = maxQuestionsApproved;
    }

    public int getPopulationSize() { return populationSize; }
    public int getGenerations() { return generations; }
    public double getMutationRate() { return mutationRate; }
    public double getCrossoverRate() { return crossoverRate; }
    public int getMinWeight() { return minWeight; }
    public int getMaxWeight() { return maxWeight; }
    public int getMaxRecentAssessments() { return maxRecentAssessments; }
    public int getMaxQuestionsApproved() { return maxQuestionsApproved; }

    /**
     * Valores iniciais (ponto de partida para POC).
     * - populationSize: razoável para média de ~50-200 recruiters; ajustes dependem do cenário.
     * - generations: iterações do GA (10..50 é razoável para POC).
     * - mutationRate: pequena (ex.: 0.05)
     * - crossoverRate: alta o suficiente para exploração (ex.: 0.7)
     * - min/max weight: 0..10 (convenção)
     * - normalizadores: valores heurísticos para janelas.
     */
    public static GeneticConfig defaults() {
        return new GeneticConfig(
                50, 30, 0.05, 0.7,
                0, 10, 20, 50
        );
    }

}
