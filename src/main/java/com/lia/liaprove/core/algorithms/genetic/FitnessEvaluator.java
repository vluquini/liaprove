package com.lia.liaprove.core.algorithms.genetic;

/**
 * Calcula fitness para um indivíduo dado o Recruiter.
 * Implementações podem usar o gene candidato do indivíduo e métricas agregadas
 * como uso recente, uso histórico, questões aprovadas, reputação e peso atual.
 */
public interface FitnessEvaluator {
    /**
     * Avalia o fitness do indivíduo associado a um recruiter.
     * Deve retornar valor finito normalizado em [0,1], onde 1.0 é o melhor.
     *
     * @param individual cromossomo com peso de voto candidato normalizado
     * @param metrics métricas agregadas do recruiter
     * @return fitness [0.0..1.0]
     */
    double evaluate(Individual individual, RecruiterMetrics metrics);
}
