package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.usecases.question.GetQuestionByIdUseCase;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do caso de uso para recuperar uma questão pelo seu ID.
 */
public class GetQuestionByIdUseCaseImpl implements GetQuestionByIdUseCase {

    private final QuestionGateway questionGateway;

    public GetQuestionByIdUseCaseImpl(QuestionGateway questionGateway) {
        this.questionGateway = questionGateway;
    }

    @Override
    public Optional<Question> execute(UUID questionId) {
        return questionGateway.findById(questionId);
    }

}
