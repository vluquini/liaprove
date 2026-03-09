package com.lia.liaprove.core.exceptions.question;

public class QuestionPreAnalysisException extends RuntimeException {

    public QuestionPreAnalysisException(String message) {
        super(message);
    }

    public QuestionPreAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
