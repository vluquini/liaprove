package com.lia.liaprove.application.services.algorithms.genetic;

import com.lia.liaprove.application.gateways.algorithms.genetic.GeneticGateway;
import com.lia.liaprove.core.algorithms.genetic.FitnessEvaluator;
import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.algorithms.genetic.Individual;
import com.lia.liaprove.core.algorithms.genetic.RecruiterMetrics;
import com.lia.liaprove.core.usecases.algorithms.genetic.GeneticAlgorithmUseCase;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação do usecase de GA usando GeneticGateway como fonte de métricas.
 * - runAdjustVoteWeights(): obtém métricas e executa GA.
 * - runAdjustVoteWeights(List<RecruiterMetrics>): versão para testes / dry-run (não persiste por si só).
 *
 * Nota: esta implementação é propositalmente simples (tournament selection, average crossover).
 */
public class GeneticAlgorithmUseCaseImpl implements GeneticAlgorithmUseCase {
    private final GeneticConfig config;
    private final FitnessEvaluator evaluator;
    private final GeneticGateway geneticGateway;
    private final Random random;

    public GeneticAlgorithmUseCaseImpl(GeneticConfig config, FitnessEvaluator evaluator, GeneticGateway geneticGateway, Random random) {
        this.config = Objects.requireNonNull(config);
        this.evaluator = Objects.requireNonNull(evaluator);
        this.geneticGateway = Objects.requireNonNull(geneticGateway);
        this.random =  Objects.requireNonNull(random);
    }

    @Override
    public Map<UUID, Integer> runAdjustVoteWeights() {
        List<RecruiterMetrics> metrics = geneticGateway.fetchAllRecruiterMetrics();
        return runAdjustVoteWeights(metrics);
    }

    @Override
    public Map<UUID, Integer> runAdjustVoteWeights(List<RecruiterMetrics> seedMetrics) {
        if (seedMetrics == null || seedMetrics.isEmpty()) return Collections.emptyMap();

        Map<UUID, RecruiterMetrics> metricsById = seedMetrics.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(RecruiterMetrics::getRecruiterId, m -> m, (first, ignored) -> first, LinkedHashMap::new));

        if (metricsById.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1) create initial population (one individual per recruiter)
        List<Individual> population = metricsById.values().stream()
                .map(m -> {
                    int current = m.getCurrentVoteWeight() == null ? config.getMinWeight() : m.getCurrentVoteWeight();
                    double normalized = normalize(current, config.getMinWeight(), config.getMaxWeight());
                    return new Individual(m.getRecruiterId(), normalized);
                })
                .collect(Collectors.toList());

        // ensure population size (pad/trim)
        int effectivePopulationSize = Math.max(config.getPopulationSize(), metricsById.size());
        if (population.size() < effectivePopulationSize) {
            while (population.size() < effectivePopulationSize) {
                Individual base = population.get(this.random.nextInt(population.size()));
                population.add(new Individual(base.getRecruiterId(), clamp(base.getGene() + noise())));
            }
        }

        // evaluate initial
        evaluatePopulation(population, metricsById);

        // evolve
        for (int gen = 0; gen < config.getGenerations(); gen++) {
            population = nextGeneration(population);
            evaluatePopulation(population, metricsById);
        }

        Map<UUID, Individual> bestByRecruiter = new HashMap<>();
        for (Individual ind : population) {
            if (metricsById.containsKey(ind.getRecruiterId())) {
                bestByRecruiter.merge(
                        ind.getRecruiterId(),
                        ind,
                        (current, candidate) -> candidate.getFitness() > current.getFitness() ? candidate : current
                );
            }
        }

        Map<UUID, Integer> newWeights = new HashMap<>();
        for (Map.Entry<UUID, Individual> entry : bestByRecruiter.entrySet()) {
            int weight = denormalizeAndClamp(entry.getValue().getGene(), config.getMinWeight(), config.getMaxWeight());
            newWeights.put(entry.getKey(), weight);
        }

        // fallback: keep original when missing
        metricsById.values().forEach(m -> newWeights.putIfAbsent(
                m.getRecruiterId(),
                Math.clamp(m.getCurrentVoteWeight() == null ? config.getMinWeight() : m.getCurrentVoteWeight(), config.getMinWeight(), config.getMaxWeight())
        ));

        return newWeights;
    }

    // helpers (evaluate, GA internals)

    private void evaluatePopulation(List<Individual> population, Map<UUID, RecruiterMetrics> metricsById) {
        for (Individual ind : population) {
            RecruiterMetrics m = metricsById.get(ind.getRecruiterId());
            double fitness = (m == null) ? 0.5 : evaluator.evaluate(ind, m);
            ind.setFitness(fitness);
        }
    }

    private List<Individual> nextGeneration(List<Individual> pop) {
        List<Individual> next = new ArrayList<>();
        while (next.size() < pop.size()) {
            Individual a = tournament(pop, 3);
            Individual b = tournament(pop, 3);
            Individual child = (this.random.nextDouble() < config.getCrossoverRate()) ? crossover(a, b) : cloneIndividual(a);
            mutate(child);
            next.add(child);
        }
        return next;
    }

    private Individual tournament(List<Individual> pop, int k) {
        Individual best = null;
        for (int i = 0; i < k; i++) {
            Individual cand = pop.get(this.random.nextInt(pop.size()));
            if (best == null || cand.getFitness() > best.getFitness()) best = cand;
        }
        return best;
    }

    private Individual crossover(Individual a, Individual b) {
        double gene = (a.getGene() + b.getGene()) / 2.0;
        return new Individual(a.getRecruiterId(), clamp(gene + noise()*0.2));
    }

    private Individual cloneIndividual(Individual a) {
        return new Individual(a.getRecruiterId(), a.getGene());
    }

    private void mutate(Individual ind) {
        if (this.random.nextDouble() < config.getMutationRate()) {
            ind.setGene(clamp(ind.getGene() + noise()));
        }
    }

    private double noise() {
        return (this.random.nextGaussian() * 0.03);
    }

    private double clamp(double v) { return Math.clamp(v, 0.0, 1.0); }

    private double normalize(double val, int min, int max) {
        if (max == min) return 0.0;
        double bounded = Math.clamp(val, min, max);
        return (bounded - min) / (double)(max - min);
    }

    private int denormalizeAndClamp(double normalized, int min, int max) {
        int w = (int) Math.round(min + normalized * (max - min));
        return Math.clamp(w, min, max);
    }
}
