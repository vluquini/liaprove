package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.usecases.question.ModerateQuestionUseCase;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do caso de uso para moderação de uma questão.
 */
public class ModerateQuestionUseCaseImpl implements ModerateQuestionUseCase {

    private final QuestionGateway questionGateway;

    public ModerateQuestionUseCaseImpl(QuestionGateway questionGateway) {
        this.questionGateway = questionGateway;
    }

    @Override
    public Optional<Question> execute(UUID questionId, ModerateQuestionCommand command) {
        return questionGateway.findById(questionId).map(question -> {
            question.setStatus(command.newStatus());
            return questionGateway.update(question);
        });
    }

}
