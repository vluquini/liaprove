package com.lia.liaprove.core.exceptions.assessment;

/**
 * Exceção lançada quando o número máximo de participantes para uma avaliação foi atingido.
 */
public class MaxAttemptsReachedException extends RuntimeException {
    public MaxAttemptsReachedException(String message) {
        super(message);
    }
}
