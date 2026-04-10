package com.lia.liaprove.infrastructure.tasks;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.usecases.assessments.EvaluateCommunityReviewAssessmentAttemptUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler do fluxo de decisão comunitária de mini-projetos.
 * Tentativas finalizadas entram em votação comunitária logo após a submissão,
 * possuem janela funcional de 7 dias e, por pragmatismo de apresentação do TCC,
 * são processadas a cada 5 minutos por uma implementação mock até a integração
 * da lógica bayesiana real.
 */
@Component
public class CommunityReviewAssessmentAttemptScheduler {

    private static final int COMMUNITY_REVIEW_WINDOW_DAYS = 7;

    private final AssessmentAttemptGateway assessmentAttemptGateway;
    private final EvaluateCommunityReviewAssessmentAttemptUseCase evaluateCommunityReviewAssessmentAttemptUseCase;

    public CommunityReviewAssessmentAttemptScheduler(
            AssessmentAttemptGateway assessmentAttemptGateway,
            EvaluateCommunityReviewAssessmentAttemptUseCase evaluateCommunityReviewAssessmentAttemptUseCase
    ) {
        this.assessmentAttemptGateway = assessmentAttemptGateway;
        this.evaluateCommunityReviewAssessmentAttemptUseCase = evaluateCommunityReviewAssessmentAttemptUseCase;
    }

    @Scheduled(fixedRate = 300000)
    public void evaluateExpiredCommunityReviewAttempts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(COMMUNITY_REVIEW_WINDOW_DAYS);
        List<AssessmentAttempt> eligibleAttempts = assessmentAttemptGateway
                .findCompletedSystemProjectAttemptsReadyForCommunityDecision(cutoff);

        for (AssessmentAttempt attempt : eligibleAttempts) {
            evaluateCommunityReviewAssessmentAttemptUseCase.execute(attempt.getId());
        }
    }
}
