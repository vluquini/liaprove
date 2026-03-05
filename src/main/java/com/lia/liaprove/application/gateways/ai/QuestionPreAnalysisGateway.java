package com.lia.liaprove.application.gateways.ai;

import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;

/**
 * Porta de integração com provedor de IA para pré-análise de questões.
 * Implementação concreta deve ficar na infraestrutura.
 */
public interface QuestionPreAnalysisGateway {
    PreAnalyzeQuestionUseCase.PreAnalysisResult analyze(PreAnalyzeQuestionUseCase.PreAnalysisCommand command);
}
