package com.lia.liaprove.core.exceptions.assessment;

/**
 * Exceção lançada quando um usuário tenta iniciar uma avaliação que já realizou.
 */
public class UserAlreadyAttemptedAssessmentException extends RuntimeException {
    public UserAlreadyAttemptedAssessmentException(String message) {
        super(message);
    }
}
