package com.lia.liaprove.core.exceptions;

/**
 * Exceção lançada quando um usuário tenta iniciar uma avaliação que já realizou.
 */
public class UserAlreadyAttemptedAssessmentException extends RuntimeException {
    public UserAlreadyAttemptedAssessmentException(String message) {
        super(message);
    }
}
