package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
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

        // Check for existing question by description BEFORE creating the new question object
        if (questionGateway.existsByDescription(dto.description())) {
            throw new InvalidUserDataException("Unable to process question submission with the provided details.");
        }

        Question newQuestion;

        if (dto.alternatives() != null && !dto.alternatives().isEmpty()) {
            newQuestion = questionFactory.createMultipleChoice(dto);
        }else
            newQuestion = questionFactory.createProject(dto);

        return questionGateway.save(newQuestion);
    }
}
