package com.lia.liaprove.infrastructure.entities.algorithms.genetic;

/**
 * Define se um multiplicador de votos se aplica globalmente a uma função (todos os recrutadores)
 * ou apenas para um recrutador específico, permitindo uma substituição padrão mais por recrutador.
 */
public enum VoteMultiplierScope {
    ROLE,
    RECRUITER
}
