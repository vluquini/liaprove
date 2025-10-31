package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.question.Alternative;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Interface para o caso de uso de submissão de uma nova questão.
 * Define o contrato para a criação de questões no sistema.
 */
public interface SubmitQuestionUseCase {

    /**
     * Executa a submissão de uma nova questão.
     *
     * @param command Objeto de comando contendo os dados necessários para criar a questão.
     * @return A questão criada e persistida no sistema.
     */
    Question execute(SubmitQuestionCommand command);

    /**
     * Objeto de comando (DTO) para a submissão de uma questão.
     * Contém todos os dados necessários para a criação de uma nova questão.
     */
    record SubmitQuestionCommand(
            String title,
            String description,
            UUID authorId,
            Set<KnowledgeArea> knowledgeAreas,
            DifficultyLevel difficultyLevel,
            RelevanceLevel relevanceByCommunity,
            LocalDateTime submissionDate,
            QuestionStatus status,
            RelevanceLevel relevanceByLLM,
            int recruiterUsageCount,
            QuestionType questionType, // Para diferenciar MultipleChoiceQuestion de ProjectQuestion
            List<Alternative> alternatives, // Apenas para MultipleChoiceQuestion
            String projectUrl // Apenas para ProjectQuestion
    ) {}

    /**
     * Enumeração para definir o tipo de questão a ser submetida.
     */
    enum QuestionType {
        MULTIPLE_CHOICE,
        PROJECT
    }
}
