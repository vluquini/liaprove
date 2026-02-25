package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.exceptions.AssessmentNotActiveException;
import com.lia.liaprove.core.exceptions.MaxAttemptsReachedException;
import com.lia.liaprove.core.exceptions.UserAlreadyAttemptedAssessmentException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.exceptions.AssessmentNotFoundException;

import java.util.Set;
import java.util.UUID;

/**
 * Inicia uma nova tentativa de avaliação (AssessmentAttempt).
 * <p>
 * Ponto de entrada para um usuário começar uma avaliação, lidando com a lógica
 * para avaliações personalizadas (via token) e do sistema (via critérios).
 */
public interface StartNewAssessmentUseCase {

    /**
     * Valida as regras de negócio e cria uma nova tentativa de avaliação.
     *
     * @param userId          ID do usuário que realiza a tentativa.
     * @param shareableToken  Token de acesso para uma avaliação personalizada (opcional).
     * @param knowledgeAreas  Critérios de área para uma avaliação do sistema (opcional).
     * @param difficultyLevel Critério de dificuldade para uma avaliação do sistema (opcional).
     * @return A entidade AssessmentAttempt criada com o status IN_PROGRESS.
     * @throws UserNotFoundException                   Se o usuário não for encontrado.
     * @throws AssessmentNotFoundException             Se a avaliação não for encontrada.
     * @throws AssessmentNotActiveException            Se a avaliação não está ativa ou expirou.
     * @throws UserAlreadyAttemptedAssessmentException Se o usuário já realizou esta avaliação.
     * @throws MaxAttemptsReachedException             Se o limite de participantes foi atingido.
     * @throws IllegalArgumentException                Se os parâmetros forem insuficientes.
     */
    AssessmentAttempt execute(UUID userId, String shareableToken,
                              Set<KnowledgeArea> knowledgeAreas, DifficultyLevel difficultyLevel);
}
