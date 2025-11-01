package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.usecases.question.ListQuestionsUseCase;

import java.util.List;

/**
 * Implementação do caso de uso para listar questões com filtros e paginação.
 */
public class ListQuestionsUseCaseImpl implements ListQuestionsUseCase {

    private final QuestionGateway questionGateway;

    public ListQuestionsUseCaseImpl(QuestionGateway questionGateway) {
        this.questionGateway = questionGateway;
    }

    @Override
    public List<Question> execute(ListQuestionsQuery query) {
        return questionGateway.findAll(
                query.knowledgeAreas(),
                query.difficultyLevel(),
                query.status(),
                query.authorId(),
                query.page(),
                query.size()
        );
    }

}
