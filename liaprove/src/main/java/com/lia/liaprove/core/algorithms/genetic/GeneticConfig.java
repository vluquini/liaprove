package com.lia.liaprove.core.algorithms.genetic;

/**
 * Parâmetros configuráveis do GA.
 */
public class GeneticConfig {
    private final int populationSize;
    private final int generations;
    private final double mutationRate;    // ex.: 0.05
    private final double crossoverRate;   // ex.: 0.7
    private final int minWeight;          // ex.: 0
    private final int maxWeight;          // ex.: 100

    // Coeficientes para avaliação
    private final double alphaUsage;   // ex: 0.6
    private final double betaRating;   // ex: 0.3
    private final double gammaCurrent; // ex: 0.1

    public GeneticConfig(int populationSize, int generations, double mutationRate, double crossoverRate, int minWeight,
                         int maxWeight, double alphaUsage, double betaRating, double gammaCurrent) {
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.alphaUsage = alphaUsage;
        this.betaRating = betaRating;
        this.gammaCurrent = gammaCurrent;
    }

    public int getPopulationSize() { return populationSize; }
    public int getGenerations() { return generations; }
    public double getMutationRate() { return mutationRate; }
    public double getCrossoverRate() { return crossoverRate; }
    public int getMinWeight() { return minWeight; }
    public int getMaxWeight() { return maxWeight; }

    public double getAlphaUsage() { return alphaUsage; }
    public double getBetaRating() { return betaRating; }
    public double getGammaCurrent() { return gammaCurrent; }

    // Factory with example defaults
    public static GeneticConfig defaults() {
        return new GeneticConfig(50, 50, 0.05, 0.7, 0, 100, 0.6, 0.3, 0.1);
    }
}
