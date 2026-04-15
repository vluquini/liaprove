package com.lia.liaprove.infrastructure.mappers.question;

import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.infrastructure.dtos.question.OpenQuestionResponse;
import com.lia.liaprove.infrastructure.entities.question.OpenQuestionEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestionMapperTest {

    private final QuestionMapper mapper = Mappers.getMapper(QuestionMapper.class);

    @Test
    void shouldMapOpenQuestionDomainToEntityAndBack() {
        OpenQuestion domain = openQuestion();

        OpenQuestionEntity entity = (OpenQuestionEntity) mapper.toEntity(domain);

        assertEquals(domain.getGuideline(), entity.getGuideline());
        assertEquals(domain.getVisibility(), entity.getVisibility());
        assertEquals(domain.getTitle(), entity.getTitle());
        assertEquals(domain.getQuestionType(), QuestionType.OPEN);

        OpenQuestion mappedBack = (OpenQuestion) mapper.toDomain(entity);

        assertEquals(domain.getGuideline(), mappedBack.getGuideline());
        assertEquals(domain.getVisibility(), mappedBack.getVisibility());
        assertEquals(domain.getTitle(), mappedBack.getTitle());
        assertEquals(QuestionType.OPEN, mappedBack.getQuestionType());
    }

    @Test
    void shouldMapOpenQuestionToOpenQuestionResponse() {
        OpenQuestion domain = openQuestion();

        OpenQuestionResponse response = (OpenQuestionResponse) mapper.toResponseDto(domain);

        assertEquals(domain.getGuideline(), response.getGuideline());
        assertEquals(domain.getVisibility(), response.getVisibility());
        assertEquals(domain.getTitle(), response.getTitle());
        assertEquals(domain.getDescription(), response.getDescription());
    }

    @Test
    void shouldResolveOpenQuestionEntityClass() {
        assertEquals(OpenQuestionEntity.class, mapper.mapToEntityClass(OpenQuestion.class));
    }

    private OpenQuestion openQuestion() {
        OpenQuestion question = new OpenQuestion("Explain the expected answer rubric.", OpenQuestionVisibility.SHARED);
        question.setId(UUID.randomUUID());
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Explain architecture trade-offs");
        question.setDescription("Describe the trade-offs of using single-table inheritance.");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.FOUR);
        question.setSubmissionDate(LocalDateTime.of(2026, 4, 15, 10, 0));
        question.setVotingEndDate(LocalDateTime.of(2026, 4, 22, 10, 0));
        question.setStatus(QuestionStatus.VOTING);
        question.setRelevanceByLLM(RelevanceLevel.THREE);
        question.setRecruiterUsageCount(2);
        return question;
    }
}
