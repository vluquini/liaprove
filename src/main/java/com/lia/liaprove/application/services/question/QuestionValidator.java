package com.lia.liaprove.application.services.question;

import com.lia.liaprove.core.domain.question.QuestionContent;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.exceptions.user.InvalidUserDataException;

import java.util.Objects;

/**
 * Validador utilitário para conteúdos de questões.
 * Centraliza as regras de negócio comuns a diferentes fluxos (criação, análise, submissão).
 */
public class QuestionValidator {

    private QuestionValidator() {
        // Utility class
    }

    /**
     * Valida os campos obrigatórios e restrições de tamanho de uma questão.
     *
     * @param content o conteúdo da questão a ser validado
     * @throws InvalidUserDataException caso alguma regra de validação seja violada
     * @throws NullPointerException caso o parâmetro content seja nulo
     */
    public static void validate(QuestionContent content) {
        validateCommon(content);
    }

    public static void validate(QuestionCreateDto content) {
        validateCommon(content);

        if (content.questionType() == null) {
            throw new InvalidUserDataException("Question type is required.");
        }

        validateOpenQuestion(content);
    }

    private static void validateCommon(QuestionContent content) {
        Objects.requireNonNull(content, "Question content must not be null");

        if (isBlank(content.title()) || content.title().length() < 10 || content.title().length() > 255) {
            throw new InvalidUserDataException("Title must have between 10 and 255 characters.");
        }
        if (isBlank(content.description()) || content.description().length() < 20 || content.description().length() > 2000) {
            throw new InvalidUserDataException("Description must have between 20 and 2000 characters.");
        }
        if (content.knowledgeAreas() == null || content.knowledgeAreas().isEmpty()) {
            throw new InvalidUserDataException("Knowledge areas must not be empty.");
        }
        if (content.difficultyByCommunity() == null) {
            throw new InvalidUserDataException("Difficulty level is required.");
        }
        if (content.relevanceByCommunity() == null) {
            throw new InvalidUserDataException("Relevance level is required.");
        }
    }

    private static void validateOpenQuestion(QuestionCreateDto content) {
        if (content.questionType() != QuestionType.OPEN) {
            return;
        }

        if (content.visibility() == null) {
            throw new InvalidUserDataException("Visibility is required for open questions.");
        }

        if (content.alternatives() != null && !content.alternatives().isEmpty()) {
            throw new InvalidUserDataException("Open questions must not have alternatives.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
