package com.lia.liaprove.core.algorithms.genetic;

import com.lia.liaprove.core.domain.user.UserRecruiter;

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
    double evaluate(Individual individual, UserRecruiter recruiter);
}
