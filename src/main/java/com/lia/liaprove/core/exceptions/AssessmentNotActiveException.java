package com.lia.liaprove.core.exceptions;

/**
 * Exceção lançada ao tentar iniciar uma avaliação que não está ativa ou já expirou.
 */
public class AssessmentNotActiveException extends RuntimeException {
    public AssessmentNotActiveException(String message) {
        super(message);
    }
}
