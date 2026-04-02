package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;

/**
 * Implementação do caso de uso de pré-análise de questão com IA.
 */
public class PreAnalyzeQuestionUseCaseImpl implements PreAnalyzeQuestionUseCase {

    private final QuestionPreAnalysisGateway questionPreAnalysisGateway;

    public PreAnalyzeQuestionUseCaseImpl(QuestionPreAnalysisGateway questionPreAnalysisGateway) {
        this.questionPreAnalysisGateway = questionPreAnalysisGateway;
    }

    @Override
    public PreAnalysisResult execute(PreAnalysisCommand command) {
        QuestionValidator.validate(command);
        return questionPreAnalysisGateway.analyze(command);
    }
}
