package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.QuestionType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenerateSystemAssessmentUseCaseImplTest {

    private final QuestionGateway questionGateway = mock(QuestionGateway.class);
    private final GenerateSystemAssessmentUseCaseImpl useCase = new GenerateSystemAssessmentUseCaseImpl(questionGateway);

    @Test
    void shouldRequestOnlyEligibleFinishedMultipleChoiceQuestionsForRequester() {
        UUID requesterId = UUID.randomUUID();

        when(questionGateway.findRandomEligibleByCriteria(
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.EASY,
                QuestionStatus.FINISHED,
                7,
                QuestionType.MULTIPLE_CHOICE,
                requesterId
        )).thenReturn(List.of());
        when(questionGateway.findRandomEligibleByCriteria(
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                QuestionStatus.FINISHED,
                2,
                QuestionType.MULTIPLE_CHOICE,
                requesterId
        )).thenReturn(List.of());
        when(questionGateway.findRandomEligibleByCriteria(
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.HARD,
                QuestionStatus.FINISHED,
                1,
                QuestionType.MULTIPLE_CHOICE,
                requesterId
        )).thenReturn(List.of());

        useCase.createQuestions(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.EASY,
                SystemAssessmentType.MULTIPLE_CHOICE,
                requesterId
        );

        verify(questionGateway).findRandomEligibleByCriteria(
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.EASY,
                QuestionStatus.FINISHED,
                7,
                QuestionType.MULTIPLE_CHOICE,
                requesterId
        );
        verify(questionGateway).findRandomEligibleByCriteria(
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                QuestionStatus.FINISHED,
                2,
                QuestionType.MULTIPLE_CHOICE,
                requesterId
        );
        verify(questionGateway).findRandomEligibleByCriteria(
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.HARD,
                QuestionStatus.FINISHED,
                1,
                QuestionType.MULTIPLE_CHOICE,
                requesterId
        );
    }

    @Test
    void shouldRequestOnlyEligibleFinishedProjectQuestionForRequester() {
        UUID requesterId = UUID.randomUUID();

        when(questionGateway.findRandomEligibleByCriteria(
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.EASY,
                QuestionStatus.FINISHED,
                1,
                QuestionType.PROJECT,
                requesterId
        )).thenReturn(List.of(new ProjectQuestion()));

        useCase.createQuestions(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.EASY,
                SystemAssessmentType.PROJECT,
                requesterId
        );

        verify(questionGateway).findRandomEligibleByCriteria(
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.EASY,
                QuestionStatus.FINISHED,
                1,
                QuestionType.PROJECT,
                requesterId
        );
    }
}
