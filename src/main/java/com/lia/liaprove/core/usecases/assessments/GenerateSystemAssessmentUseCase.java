package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;

import java.util.List;
import java.util.UUID;

/**
 * Interface responsável por gerar a lista de questões
 * para uma avaliação do sistema, com base em critérios específicos.
 */
public interface GenerateSystemAssessmentUseCase {
    /**
     * Cria uma lista de questões para uma nova avaliação do sistema.
     *
     * @param knowledgeArea Área de conhecimento desejada.
     * @param difficultyLevel O nível de dificuldade da avaliação (EASY, MEDIUM, HARD).
     * @param type O tipo de avaliação do sistema (MULTIPLE_CHOICE ou PROJECT).
     * @param requesterId ID do usuário solicitante, usado para excluir questões inelegíveis.
     * @return Uma lista de questões selecionadas e embaralhadas.
     */
    List<Question> createQuestions(KnowledgeArea knowledgeArea, DifficultyLevel difficultyLevel,
                                   SystemAssessmentType type, UUID requesterId);
}
