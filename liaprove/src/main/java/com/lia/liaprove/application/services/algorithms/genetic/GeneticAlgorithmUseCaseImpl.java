package com.lia.liaprove.application.services.algorithms.genetic;

import com.lia.liaprove.application.gateways.algorithms.genetic.RecruiterGateway;
import com.lia.liaprove.core.algorithms.genetic.FitnessEvaluator;
import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.algorithms.genetic.Individual;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.usecases.algorithms.genetic.GeneticAlgorithmUseCase;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação simples de um GA que ajusta voteWeight.
 *
 * Observações importantes:
 * - É propositalmente simples: população inicial deriva dos weights atuais.
 * - Fitness = alpha * normalizedUsage + beta * normalizedRating + gamma * normalizedCurrentWeight.
 *   Ajustar os coeficientes conforme necessidade.
 *
 * Campos do domínio usados:
 * - UserRecruiter.getId()                       -> UUID
 * - UserRecruiter.getRecruiterEngagementScore() -> int
 * - UserRecruiter.getRecruiterRating()          -> Float (0..5)
 * - UserRecruiter.getVoteWeight()               -> Integer (0..100)
 *
 * NOTE: Este serviço NÃO persiste diretamente. Retorna Map<UUID,Integer> com novos pesos.
 * A camada application/infrastructure deve persistir esses valores via gateway/repository.
 */
public class GeneticAlgorithmUseCaseImpl implements GeneticAlgorithmUseCase {

    private final GeneticConfig config;
    private final FitnessEvaluator evaluator;
    private final Random random = new Random();

    // Gateway para obter recruiters e persistir novos pesos
    private final RecruiterGateway recruiterGateway;

    public GeneticAlgorithmUseCaseImpl(GeneticConfig config,
                                       FitnessEvaluator evaluator,
                                       RecruiterGateway recruiterGateway) {
        this.config = Objects.requireNonNull(config);
        this.evaluator = Objects.requireNonNull(evaluator);
        this.recruiterGateway = Objects.requireNonNull(recruiterGateway);
    }

    @Override
    public Map<UUID, Integer> runAdjustVoteWeights() {
        // Se não houver recruiters, não há o que ajustar
        List<UserRecruiter> seedRecruiters = recruiterGateway.findAllRecruiters();
        if (seedRecruiters == null || seedRecruiters.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1) build initial population: one individual per recruiter, gene = currentWeightNormalized
        List<Individual> population = seedRecruiters.stream()
                .map(r -> {
                    double currentWeight = (r.getVoteWeight() == null ? 0 : r.getVoteWeight());
                    double normalized = normalize(currentWeight, config.getMinWeight(), config.getMaxWeight());
                    return new Individual(r.getId(), normalized);
                })
                .collect(Collectors.toList());

        // ensure population size: pad/trim
        if (population.size() < config.getPopulationSize()) {
            // pad by cloning existing individuals with small noise (keep recruiterId for traceability)
            while (population.size() < config.getPopulationSize()) {
                Individual base = population.get(random.nextInt(population.size()));
                // clone using same recruiterId (keeps mapping valid)
                population.add(new Individual(base.getRecruiterId(), clamp(base.getGene() + noise())));
            } // trim to best individuals by fitness (if necessary)
        } else if (population.size() > config.getPopulationSize()) {
            // ensure population has fitness evaluated (if not, evaluate quickly)
            // here we assume evaluatePopulation foi chamada antes; otherwise, call it.
            population.sort(Comparator.comparingDouble(Individual::getFitness).reversed());
            population = new ArrayList<>(population.subList(0, config.getPopulationSize()));
        }

        // 2) evaluate initial fitness
        Map<UUID, UserRecruiter> recruiterById = seedRecruiters.stream().collect(Collectors.toMap(UserRecruiter::getId, r -> r));
        evaluatePopulation(population, recruiterById);

        // 3) evolve
        for (int gen = 0; gen < config.getGenerations(); gen++) {
            population = nextGeneration(population, recruiterById);
            evaluatePopulation(population, recruiterById);
        }

        // 4) collect results: best gene per recruiter (we'll map by closest recruiter id)
        // Here we assume one individual per recruiter id present initially; simpler approach:
        Map<UUID, Integer> results = new HashMap<>();
        for (Individual ind : population) {
            // if the individual was created as a padded clone, it may not have valid recruiterId.
            if (recruiterById.containsKey(ind.getRecruiterId())) {
                int weight = denormalizeAndClamp(ind.getGene(), config.getMinWeight(), config.getMaxWeight());
                results.put(ind.getRecruiterId(), weight);
            }
        }

        // Fallback: if some recruiters missing (due to trimming), keep original weights
        seedRecruiters.forEach(r -> results.putIfAbsent(r.getId(), (r.getVoteWeight() == null ? 0 : r.getVoteWeight())));

        return results;
    }

    // --- internal helpers ----

    private void evaluatePopulation(List<Individual> population, Map<UUID, UserRecruiter> recruiters) {
        for (Individual ind : population) {
            UserRecruiter r = recruiters.get(ind.getRecruiterId());
            double fitness;
            if (r == null) {
                // padded or synthetic individual: give neutral fitness
                fitness = 0.5;
            } else {
                fitness = evaluator.evaluate(ind, r);
            }
            ind.setFitness(fitness);
        }
    }

    private List<Individual> nextGeneration(List<Individual> pop, Map<UUID, UserRecruiter> recruiters) {
        // simple tournament selection + average crossover + mutation
        List<Individual> next = new ArrayList<>();
        while (next.size() < pop.size()) {
            Individual a = tournament(pop, 3);
            Individual b = tournament(pop, 3);
            Individual child = (random.nextDouble() < config.getCrossoverRate()) ? crossover(a, b) : cloneIndividual(a);
            mutate(child);
            next.add(child);
        }
        return next;
    }

    private Individual tournament(List<Individual> pop, int k) {
        Individual best = null;
        for (int i = 0; i < k; i++) {
            Individual cand = pop.get(random.nextInt(pop.size()));
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
        if (random.nextDouble() < config.getMutationRate()) {
            ind.setGene(clamp(ind.getGene() + noise()));
        }
    }

    private double noise() {
        return (random.nextGaussian() * 0.03); // small gaussian noise
    }

    private double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    private double normalize(double val, int min, int max) {
        if (max == min) return 0.0;
        return (val - min) / (double)(max - min);
    }

    private int denormalizeAndClamp(double normalized, int min, int max) {
        int w = (int) Math.round(min + normalized * (max - min));
        return Math.max(min, Math.min(max, w));
    }
}
