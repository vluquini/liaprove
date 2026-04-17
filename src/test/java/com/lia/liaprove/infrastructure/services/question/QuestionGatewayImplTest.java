package com.lia.liaprove.infrastructure.services.question;

import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.infrastructure.entities.question.OpenQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionGatewayImplTest {

    @Mock
    private QuestionJpaRepository questionJpaRepository;

    private final QuestionMapper questionMapper = Mappers.getMapper(QuestionMapper.class);

    @Test
    void shouldSaveOpenQuestionThroughJpaEntityMapping() {
        QuestionGatewayImpl gateway = new QuestionGatewayImpl(questionJpaRepository, questionMapper);
        OpenQuestion question = openQuestion();

        when(questionJpaRepository.save(any(QuestionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Question saved = gateway.save(question);

        ArgumentCaptor<QuestionEntity> entityCaptor = ArgumentCaptor.forClass(QuestionEntity.class);
        verify(questionJpaRepository).save(entityCaptor.capture());

        OpenQuestionEntity persistedEntity = (OpenQuestionEntity) entityCaptor.getValue();
        OpenQuestion mappedBack = (OpenQuestion) saved;

        assertEquals(question.getGuideline(), persistedEntity.getGuideline());
        assertEquals(question.getVisibility(), persistedEntity.getVisibility());
        assertEquals(question.getGuideline(), mappedBack.getGuideline());
        assertEquals(question.getVisibility(), mappedBack.getVisibility());
        assertEquals(QuestionType.OPEN, mappedBack.getQuestionType());
    }

    @Test
    void shouldFindOpenQuestionById() {
        QuestionGatewayImpl gateway = new QuestionGatewayImpl(questionJpaRepository, questionMapper);
        UUID id = UUID.randomUUID();
        OpenQuestionEntity entity = openQuestionEntity(id);

        when(questionJpaRepository.findByIdFetchingAlternatives(id)).thenReturn(Optional.of(entity));

        Question found = gateway.findById(id).orElseThrow();

        assertInstanceOf(OpenQuestion.class, found);
        OpenQuestion openQuestion = (OpenQuestion) found;
        assertEquals(entity.getGuideline(), openQuestion.getGuideline());
        assertEquals(entity.getVisibility(), openQuestion.getVisibility());
        assertEquals(QuestionType.OPEN, openQuestion.getQuestionType());
    }

    @Test
    void shouldRouteOpenQuestionClassToOpenQuestionEntityInRandomSearch() {
        QuestionGatewayImpl gateway = new QuestionGatewayImpl(questionJpaRepository, questionMapper);
        Set<KnowledgeArea> areas = Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT);

        when(questionJpaRepository.findRandomQuestionIds(anySet(), any(DifficultyLevel.class), any(Class.class), any(QuestionStatus.class), any(Pageable.class)))
                .thenReturn(List.of());

        gateway.findRandomByCriteria(areas, DifficultyLevel.EASY, QuestionStatus.VOTING, 3, OpenQuestion.class);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
        verify(questionJpaRepository).findRandomQuestionIds(anySet(), eq(DifficultyLevel.EASY), classCaptor.capture(), eq(QuestionStatus.VOTING), any(Pageable.class));
        assertEquals(OpenQuestionEntity.class, classCaptor.getValue());
    }

    private OpenQuestion openQuestion() {
        OpenQuestion question = new OpenQuestion("Use the rubric to justify your answer.", OpenQuestionVisibility.PRIVATE);
        question.setId(UUID.randomUUID());
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Describe JPA inheritance");
        question.setDescription("Explain how the open question subtype is stored.");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.DATABASE));
        question.setDifficultyByCommunity(DifficultyLevel.HARD);
        question.setRelevanceByCommunity(RelevanceLevel.FIVE);
        question.setSubmissionDate(LocalDateTime.of(2026, 4, 15, 9, 30));
        question.setVotingEndDate(LocalDateTime.of(2026, 4, 22, 9, 30));
        question.setStatus(QuestionStatus.VOTING);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(1);
        return question;
    }

    private OpenQuestionEntity openQuestionEntity(UUID id) {
        OpenQuestionEntity entity = new OpenQuestionEntity();
        entity.setId(id);
        entity.setAuthorId(UUID.randomUUID());
        entity.setTitle("Describe JPA inheritance");
        entity.setDescription("Explain how the open question subtype is stored.");
        entity.setKnowledgeAreas(Set.of(KnowledgeArea.DATABASE));
        entity.setDifficultyByCommunity(DifficultyLevel.HARD);
        entity.setRelevanceByCommunity(RelevanceLevel.FIVE);
        entity.setSubmissionDate(LocalDateTime.of(2026, 4, 15, 9, 30));
        entity.setVotingEndDate(LocalDateTime.of(2026, 4, 22, 9, 30));
        entity.setStatus(QuestionStatus.VOTING);
        entity.setRelevanceByLLM(RelevanceLevel.FOUR);
        entity.setRecruiterUsageCount(1);
        entity.setGuideline("Use the rubric to justify your answer.");
        entity.setVisibility(OpenQuestionVisibility.PRIVATE);
        return entity;
    }
}
