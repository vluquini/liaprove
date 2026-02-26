package com.lia.liaprove.core.exceptions.assessment;

/**
 * Exceção lançada quando se tenta remover uma Personalized Assessment
 * que já possui um Attempt feito por um usuário.
 */
public class AssessmentInUseException extends RuntimeException {
    public AssessmentInUseException(String message) {
        super(message);
    }
}
