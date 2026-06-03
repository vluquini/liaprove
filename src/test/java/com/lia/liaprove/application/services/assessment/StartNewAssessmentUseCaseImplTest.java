package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.usecases.assessments.GenerateSystemAssessmentUseCase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StartNewAssessmentUseCaseImplTest {

    private final AssessmentGateway assessmentGateway = mock(AssessmentGateway.class);
    private final AssessmentAttemptGateway attemptGateway = mock(AssessmentAttemptGateway.class);
    private final UserGateway userGateway = mock(UserGateway.class);
    private final GenerateSystemAssessmentUseCase generateSystemAssessmentUseCase = mock(GenerateSystemAssessmentUseCase.class);
    private final StartNewAssessmentUseCaseImpl useCase = new StartNewAssessmentUseCaseImpl(
            assessmentGateway,
            attemptGateway,
            userGateway,
            generateSystemAssessmentUseCase
    );

    @Test
    void shouldPassRequesterIdWhenGeneratingSystemAssessmentQuestions() {
        UUID userId = UUID.randomUUID();
        UserProfessional user = new UserProfessional();
        user.setId(userId);

        when(userGateway.findById(userId)).thenReturn(Optional.of(user));
        when(generateSystemAssessmentUseCase.createQuestions(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.EASY,
                SystemAssessmentType.PROJECT,
                userId
        )).thenReturn(List.of(new ProjectQuestion()));
        when(attemptGateway.save(any(AssessmentAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(
                userId,
                null,
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.EASY,
                SystemAssessmentType.PROJECT
        );

        verify(generateSystemAssessmentUseCase).createQuestions(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.EASY,
                SystemAssessmentType.PROJECT,
                userId
        );
    }
}
