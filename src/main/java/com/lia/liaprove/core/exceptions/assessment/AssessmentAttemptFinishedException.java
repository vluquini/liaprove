package com.lia.liaprove.core.exceptions.assessment;

/**
 * Exceção lançada quando se tenta modificar ou submeter respostas para uma tentativa
 * de avaliação que já foi finalizada.
 */
public class AssessmentAttemptFinishedException extends RuntimeException {
    public AssessmentAttemptFinishedException(String message) {
        super(message);
    }
}
