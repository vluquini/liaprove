package com.lia.liaprove.core.exceptions.question;

/**
 * Exceção para falhas na etapa de pré-análise de questões com IA.
 */
public class QuestionPreAnalysisException extends RuntimeException {
    public QuestionPreAnalysisException(String message) {
        super(message);
    }

    public QuestionPreAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
