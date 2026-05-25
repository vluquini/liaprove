package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.usecases.question.ListQuestionsUseCase;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação do caso de uso para listar questões com filtros e paginação.
 */
public class ListQuestionsUseCaseImpl implements ListQuestionsUseCase {

    private static final int AUTHOR_NAME_SEARCH_LIMIT = 1000;

    private final QuestionGateway questionGateway;
    private final UserGateway userGateway;

    public ListQuestionsUseCaseImpl(QuestionGateway questionGateway, UserGateway userGateway) {
        this.questionGateway = questionGateway;
        this.userGateway = userGateway;
    }

    @Override
    public List<Question> execute(ListQuestionsQuery query) {
        Set<UUID> authorIds = resolveAuthorIds(query.authorName());

        if (authorIds != null && authorIds.isEmpty()) {
            return List.of();
        }

        return questionGateway.findAll(
                query.knowledgeAreas(),
                query.difficultyLevel(),
                query.status(),
                query.authorId(),
                authorIds,
                query.page(),
                query.size()
        );
    }

    private Set<UUID> resolveAuthorIds(String authorName) {
        if (authorName == null || authorName.isBlank()) {
            return null;
        }

        return userGateway.search(authorName.trim(), null, 0, AUTHOR_NAME_SEARCH_LIMIT)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }

}
