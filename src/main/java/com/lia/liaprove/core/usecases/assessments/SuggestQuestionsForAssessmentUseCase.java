package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.core.domain.assessment.SuggestionCriteria;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para sugerir questões a um recrutador para a criação de avaliações.
 */
public interface SuggestQuestionsForAssessmentUseCase {
    /**
     * Executa a lógica de sugestão de questões com base nos critérios fornecidos.
     *
     * @param recruiterId O ID do recrutador solicitando as sugestões.
     * @param criteria    Os critérios de filtro (áreas de conhecimento, dificuldade, limite).
     * @return Uma lista de {@link ScoredQuestion} ordenada por relevância.
     * @throws UserNotFoundException  se o ID do recrutador não for encontrado.
     * @throws AuthorizationException se o usuário não for um recrutador.
     */
    List<ScoredQuestion> execute(UUID recruiterId, SuggestionCriteria criteria);
}
