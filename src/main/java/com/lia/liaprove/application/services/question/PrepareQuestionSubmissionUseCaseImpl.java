package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.core.exceptions.user.InvalidUserDataException;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;

import java.util.Objects;

public class PrepareQuestionSubmissionUseCaseImpl implements PrepareQuestionSubmissionUseCase {

    private final QuestionPreAnalysisGateway questionPreAnalysisGateway;

    public PrepareQuestionSubmissionUseCaseImpl(QuestionPreAnalysisGateway questionPreAnalysisGateway) {
        this.questionPreAnalysisGateway = questionPreAnalysisGateway;
    }

    @Override
    public PreparedQuestion execute(PreparationCommand command) {
        Objects.requireNonNull(command, "Command must not be null");
        validate(command);
        return questionPreAnalysisGateway.prepareForSubmission(command);
    }

    private static void validate(PreparationCommand command) {
        if (command.title() == null || command.title().isBlank()) {
            throw new InvalidUserDataException("Title must not be empty.");
        }
        if (command.description() == null || command.description().isBlank()) {
            throw new InvalidUserDataException("Description must not be empty.");
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
}
