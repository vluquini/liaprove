package com.lia.liaprove.infrastructure.tasks;

import com.lia.liaprove.core.usecases.question.PublishApprovedQuestionsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApprovedQuestionPublicationSchedulerTest {

    @Mock
    private PublishApprovedQuestionsUseCase publishApprovedQuestionsUseCase;

    @Test
    void shouldDelegatePublicationToUseCaseWithConfiguredDelay() {
        ApprovedQuestionPublicationScheduler scheduler = new ApprovedQuestionPublicationScheduler(
                publishApprovedQuestionsUseCase,
                Duration.ofDays(7)
        );

        scheduler.publishApprovedQuestions();

        verify(publishApprovedQuestionsUseCase).publishEligibleQuestions(eq(Duration.ofDays(7)), any(LocalDateTime.class));
    }

    @Test
    void shouldSupportShortDelayForDemonstrationProfiles() {
        ApprovedQuestionPublicationScheduler scheduler = new ApprovedQuestionPublicationScheduler(
                publishApprovedQuestionsUseCase,
                Duration.ofMinutes(5)
        );

        scheduler.publishApprovedQuestions();

        verify(publishApprovedQuestionsUseCase).publishEligibleQuestions(eq(Duration.ofMinutes(5)), any(LocalDateTime.class));
    }
}
