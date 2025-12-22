package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.usecases.question.QuestionFactory;
import com.lia.liaprove.core.usecases.question.SubmitQuestionUseCase;

import java.util.List;
import java.util.Objects;

/**
 * Implementação do caso de uso para submissão de uma nova questão.
 */
public class SubmitQuestionUseCaseImpl implements SubmitQuestionUseCase {

    private final QuestionGateway questionGateway;
    private final QuestionFactory questionFactory;

    public SubmitQuestionUseCaseImpl(QuestionGateway questionGateway, QuestionFactory questionFactory) {
        this.questionGateway = questionGateway;
        this.questionFactory = questionFactory;
    }

    @Override
    public Question createMultipleChoice(QuestionCreateDto dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        // Check for existing question by description BEFORE creating the new question object
        if (questionGateway.existsByDescription(dto.description())) {
            throw new InvalidUserDataException("Unable to process question submission with the provided details.");
        }

        // Business rules specific to multiple choice
        List<Alternative> alternatives = dto.alternatives();

        if (alternatives == null || alternatives.size() < 3 || alternatives.size() > 5) {
            throw new InvalidUserDataException("Multiple choice question must have between 3 and 5 alternatives.");
        }

        long correctCount = alternatives.stream().filter(Alternative::correct).count();

        if (correctCount != 1) {
            throw new InvalidUserDataException("Exactly one alternative must be marked as correct. Found: " + correctCount);
        }

        // Delegate entity construction to factory (factory is responsible for defaults like submissionDate/status)
        MultipleChoiceQuestion question = questionFactory.createMultipleChoice(dto);

        // Persist and return
        return questionGateway.save(question);
    }

    @Override
    public Question createProject(QuestionCreateDto dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        // Check for existing question by description BEFORE creating the new question object
        if (questionGateway.existsByDescription(dto.description())) {
            throw new InvalidUserDataException("Unable to process question submission with the provided details.");
        }

        // Sanity: project creation should not carry alternatives
        List<Alternative> alternatives = dto.alternatives();
        if (alternatives != null && !alternatives.isEmpty()) {
            throw new InvalidUserDataException("Project question must not include alternatives at creation time.");
        }

        // Factory will create ProjectQuestion with defaults (projectUrl remains null)
        ProjectQuestion question = questionFactory.createProject(dto);

        // Persist and return
        return questionGateway.save(question);
    }
}
