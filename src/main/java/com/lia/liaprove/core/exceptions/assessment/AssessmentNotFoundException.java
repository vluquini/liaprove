package com.lia.liaprove.core.exceptions.assessment;

/**
 * Exceção lançada quando um assessment não é encontrado.
 */
public class AssessmentNotFoundException extends RuntimeException {
    public AssessmentNotFoundException(String message) {
        super(message);
    }
}
