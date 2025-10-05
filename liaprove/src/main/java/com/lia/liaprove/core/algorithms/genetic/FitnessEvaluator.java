package com.lia.liaprove.core.algorithms.genetic;

/**
 * Calcula fitness para um indivíduo dado o Recruiter.
 * Implementações podem usar: recruiterUsageCount, recruiterRating, current voteWeight, etc.
 *
 * Campos do domínio:
 *  - UserRecruiter.getRecruiterEngagementScore() -> int
 *  - UserRecruiter.getRecruiterRating()          -> float
 *  - UserRecruiter.getVoteWeight()               -> int
 */
public interface FitnessEvaluator {
    /**
     * Avalia o fitness do indivíduo associado a um recruiter.
     * Deve retornar valor normalizado 0..1 (onde 1.0 é o melhor).
     *
     * @param individual cromossomo (representando weight normalizado)
     * @param metrics métricas agregadas do recruiter (fonte: RecruiterGateway)
     * @return fitness [0.0..1.0]
     */
    double evaluate(Individual individual, RecruiterMetrics metrics);
}
