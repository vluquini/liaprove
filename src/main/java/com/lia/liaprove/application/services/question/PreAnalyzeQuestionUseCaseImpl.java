package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.core.exceptions.user.InvalidUserDataException;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;

import java.util.Objects;

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
        Objects.requireNonNull(command, "Command must not be null");

        validateCommand(command);
        return questionPreAnalysisGateway.analyze(command);
    }

    private static void validateCommand(PreAnalysisCommand command) {
        if (isBlank(command.title()) || command.title().length() < 10 || command.title().length() > 255) {
            throw new InvalidUserDataException("Title must have between 10 and 255 characters.");
        }
        if (isBlank(command.description()) || command.description().length() < 20 || command.description().length() > 2000) {
            throw new InvalidUserDataException("Description must have between 20 and 2000 characters.");
        }
        if (command.knowledgeAreas() == null || command.knowledgeAreas().isEmpty()) {
            throw new InvalidUserDataException("Knowledge areas must not be empty.");
        }
        if (command.difficultyByCommunity() == null) {
            throw new InvalidUserDataException("Difficulty level is required.");
        }
        if (command.relevanceByCommunity() == null) {
            throw new InvalidUserDataException("Relevance level is required.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
