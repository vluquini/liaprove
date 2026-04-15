package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.exceptions.user.InvalidUserDataException;
import com.lia.liaprove.core.usecases.question.QuestionFactory;
import com.lia.liaprove.core.usecases.question.SubmitQuestionUseCase;

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
    public Question submit(QuestionCreateDto dto) {
        Objects.requireNonNull(dto, "Data must not be null");

        if (dto.relevanceByLLM() == null) {
            throw new InvalidUserDataException("AI pre-analysis is required before question submission.");
        }

        // Check for existing question by description BEFORE creating the new question object
        if (questionGateway.existsByDescription(dto.description())) {
            throw new InvalidUserDataException("Unable to process question submission with the provided details.");
        }

        Question newQuestion = switch (dto.questionType()) {
            case MULTIPLE_CHOICE -> questionFactory.createMultipleChoice(dto);
            case PROJECT -> questionFactory.createProject(dto);
            case OPEN -> questionFactory.createOpenQuestion(dto);
        };

        return questionGateway.save(newQuestion);
    }
}
