package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublishApprovedQuestionsUseCaseImplTest {

    @Mock
    private QuestionGateway questionGateway;

    @Test
    void shouldPublishApprovedQuestionsWhoseVotingEndDateIsOlderThanPublicationDelay() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 2, 10, 0);
        Duration publicationDelay = Duration.ofMinutes(5);
        ProjectQuestion question = approvedProjectQuestion(now.minusMinutes(10));

        when(questionGateway.findByStatusAndVotingEndDateBefore(QuestionStatus.APPROVED, now.minus(publicationDelay)))
                .thenReturn(List.of(question));

        PublishApprovedQuestionsUseCaseImpl useCase = new PublishApprovedQuestionsUseCaseImpl(questionGateway);

        int publishedCount = useCase.publishEligibleQuestions(publicationDelay, now);

        assertEquals(1, publishedCount);
        assertEquals(QuestionStatus.FINISHED, question.getStatus());
        verify(questionGateway).update(question);
    }

    @Test
    void shouldNotUpdateAnythingWhenNoApprovedQuestionIsEligible() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 2, 10, 0);
        Duration publicationDelay = Duration.ofMinutes(5);

        when(questionGateway.findByStatusAndVotingEndDateBefore(QuestionStatus.APPROVED, now.minus(publicationDelay)))
                .thenReturn(List.of());

        PublishApprovedQuestionsUseCaseImpl useCase = new PublishApprovedQuestionsUseCaseImpl(questionGateway);

        int publishedCount = useCase.publishEligibleQuestions(publicationDelay, now);

        assertEquals(0, publishedCount);
        verify(questionGateway, never()).update(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldUseNowMinusPublicationDelayAsCutoff() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 2, 10, 0);
        Duration publicationDelay = Duration.ofMinutes(5);
        ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        when(questionGateway.findByStatusAndVotingEndDateBefore(org.mockito.ArgumentMatchers.eq(QuestionStatus.APPROVED),
                cutoffCaptor.capture())).thenReturn(List.of());

        PublishApprovedQuestionsUseCaseImpl useCase = new PublishApprovedQuestionsUseCaseImpl(questionGateway);

        useCase.publishEligibleQuestions(publicationDelay, now);

        assertEquals(LocalDateTime.of(2026, 6, 2, 9, 55), cutoffCaptor.getValue());
    }

    private ProjectQuestion approvedProjectQuestion(LocalDateTime votingEndDate) {
        return new ProjectQuestion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Mini-project",
                "Build a small API",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.THREE,
                LocalDateTime.of(2026, 6, 1, 10, 0),
                votingEndDate,
                QuestionStatus.APPROVED,
                RelevanceLevel.THREE,
                0
        );
    }
}
