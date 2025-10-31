package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.QuestionStatus;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Interface para o caso de uso de listagem de questões com filtros e paginação.
 */
public interface ListQuestionsUseCase {

    /**
     * Executa a listagem de questões com base nos critérios fornecidos.
     *
     * @param query Objeto de consulta contendo os filtros e informações de paginação.
     * @return Uma lista de questões que correspondem aos critérios.
     */
    List<Question> execute(ListQuestionsQuery query);

    /**
     * Objeto de consulta (DTO) para listar questões.
     * Contém os filtros e informações de paginação.
     */
    record ListQuestionsQuery(
            Set<KnowledgeArea> knowledgeAreas,
            DifficultyLevel difficultyLevel,
            QuestionStatus status,
            UUID authorId,
            int page,
            int size
    ) {}
}
