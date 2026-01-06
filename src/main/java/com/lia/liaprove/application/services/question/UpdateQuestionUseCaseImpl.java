package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.AuthorizationException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.question.UpdateQuestionUseCase;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do caso de uso para atualização de uma questão existente.
 */
public class UpdateQuestionUseCaseImpl implements UpdateQuestionUseCase {

    private final QuestionGateway questionGateway;
    private final UserGateway userGateway;

    public UpdateQuestionUseCaseImpl(QuestionGateway questionGateway, UserGateway userGateway) {
        this.questionGateway = questionGateway;
        this.userGateway = userGateway;
    }

    @Override
    public Optional<Question> execute(UUID actorId, UUID questionId, UpdateQuestionCommand command) {
        User actor = userGateway.findById(actorId)
                .orElseThrow(UserNotFoundException::new);

        if (actor.getRole() != UserRole.ADMIN) {
            throw new AuthorizationException("User does not have permission to update questions.");
        }

        return questionGateway.findById(questionId).map(question -> {
            command.title().ifPresent(question::setTitle);
            command.description().ifPresent(question::setDescription);
            command.knowledgeAreas().ifPresent(question::setKnowledgeAreas);

            // Se for uma questão de múltipla escolha, atualiza as alternativas
            if (question instanceof MultipleChoiceQuestion mcQuestion) {
                command.alternatives().ifPresent(mcQuestion::updateAlternatives);
            }

            return questionGateway.update(question);
        });
    }
}
