package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.question.Alternative;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Interface para o caso de uso de atualização de uma questão existente.
 */
public interface UpdateQuestionUseCase {

    /**
     * Executa a atualização de uma questão.
     *
     * @param questionId O ID da questão a ser atualizada.
     * @param command Objeto de comando contendo os dados para atualização da questão.
     * @return A questão atualizada, se encontrada, ou um Optional vazio caso contrário.
     */
    Optional<Question> execute(UUID questionId, UpdateQuestionCommand command);

    /**
     * Objeto de comando (DTO) para a atualização de uma questão.
     * Contém os campos que podem ser atualizados. Campos nulos indicam que não devem ser alterados.
     */
    record UpdateQuestionCommand(
            Optional<String> title,
            Optional<String> description,
            Optional<Set<KnowledgeArea>> knowledgeAreas,
            Optional<DifficultyLevel> difficultyLevel,
            Optional<RelevanceLevel> relevanceByCommunity,
            Optional<LocalDateTime> submissionDate,
            Optional<RelevanceLevel> relevanceByLLM,
            Optional<Integer> recruiterUsageCount,
            Optional<List<Alternative>> alternatives, // Apenas para MultipleChoiceQuestion
            Optional<String> projectUrl // Apenas para ProjectQuestion
    ) {}
}
