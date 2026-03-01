package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;

import java.util.List;
import java.util.Set;

/**
 * Interface responsável por gerar a lista de questões
 * para uma avaliação do sistema, com base em critérios específicos.
 */
public interface GenerateSystemAssessmentUseCase {
    /**
     * Cria uma lista de questões para uma nova avaliação do sistema.
     *
     * @param knowledgeAreas As áreas de conhecimento desejadas.
     * @param difficultyLevel O nível de dificuldade da avaliação (EASY, MEDIUM, HARD).
     * @return Uma lista de questões selecionadas e embaralhadas.
     */
    List<Question> createQuestions(Set<KnowledgeArea> knowledgeAreas, DifficultyLevel difficultyLevel);
}
