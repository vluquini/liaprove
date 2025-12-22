package com.lia.liaprove.application.services.question;

import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.domain.user.*;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.usecases.question.QuestionFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Implementação da Factory para a criação de diferentes tipos de questões.
 * Centraliza a lógica de instanciação e configuração inicial das entidades de Question.
 */
public class DefaultQuestionFactory implements QuestionFactory {

    @Override
    public MultipleChoiceQuestion createMultipleChoice(QuestionCreateDto dto) {
        validateFields(dto);

        MultipleChoiceQuestion question = new MultipleChoiceQuestion();

        initCommonFields(question, dto);

        // Mapear DTO alternatives -> domain Alternative (sem id, JPA gerará na persistência)
        List<Alternative> domainAlternatives = dto.alternatives() == null ? List.of()
                : dto.alternatives().stream()
                .map(req -> new Alternative(null, req.text(), req.correct()))
                .toList();

        question.setAlternatives(domainAlternatives);

        return question;
    }

    @Override
    public ProjectQuestion createProject(QuestionCreateDto dto) {
        validateFields(dto);

        ProjectQuestion question = new ProjectQuestion();

        initCommonFields(question, dto);

        // specific field of project questions
        question.setProjectUrl(null);

        return question;
    }

    private void validateFields(QuestionCreateDto dto) {
        Objects.requireNonNull(dto, "Question creation data cannot be null");

        if (dto.title() == null || dto.title().isBlank()) {
            throw new InvalidUserDataException("Title must not be empty");
        }
        if (dto.description() == null || dto.description().isBlank()) {
            throw new InvalidUserDataException("Description must not be empty");
        }
        if (dto.knowledgeAreas() == null || dto.knowledgeAreas().isEmpty()) {
            throw new InvalidUserDataException("Knowledge areas must not be empty");
        }
        if (dto.difficultyByCommunity() == null) {
            throw new InvalidUserDataException("Difficulty level mus be provided.");
        }
        if (dto.relevanceByCommunity() == null) {
            throw new InvalidUserDataException("Relevance level mus be provided.");
        }
    }

    private void initCommonFields(Question question, QuestionCreateDto dto) {
        question.setAuthorId(dto.authorId());
        question.setTitle(dto.title());
        question.setDescription(dto.description());
        question.setKnowledgeAreas(dto.knowledgeAreas());
        question.setDifficultyByCommunity(dto.difficultyByCommunity());
        question.setRelevanceByCommunity(dto.relevanceByCommunity());
        question.setSubmissionDate(LocalDateTime.now());
        question.setStatus(QuestionStatus.VOTING);
        question.setRelevanceByLLM(RelevanceLevel.FIVE); // temporário
        question.setRecruiterUsageCount(0);
    }

}




