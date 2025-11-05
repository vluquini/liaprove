package com.lia.liaprove.core.domain.assessment;

/**
 * Define os possíveis status para uma tentativa de avaliação (AssessmentAttempt).
 * Estes status indicam o progresso e o resultado de uma avaliação realizada por um usuário.
 */
public enum AssessmentAttemptStatus {
    IN_PROGRESS,
    COMPLETED,
    APPROVED,
    FAILED
}
