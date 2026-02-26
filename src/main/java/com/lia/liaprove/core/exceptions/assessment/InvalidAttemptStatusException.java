package com.lia.liaprove.core.exceptions.assessment;

/**
 * Exceção lançada ao tentar executar uma operação em uma tentativa de avaliação
 * que não está no estado esperado para aquela operação.
 */
public class InvalidAttemptStatusException extends RuntimeException {
    public InvalidAttemptStatusException(String message) {
        super(message);
    }
}
