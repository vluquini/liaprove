package com.lia.liaprove.infrastructure.tasks;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.usecases.assessments.EvaluateCommunityReviewAssessmentAttemptUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduler do fluxo de decisão comunitária de mini-projetos.
 * Tentativas finalizadas entram em votação comunitária logo após a submissão,
 * por pragmatismo de apresentação do TCC, são processadas uma por vez a cada
 * 5 minutos por uma implementação mock até a integração
 * da lógica bayesiana real.
 */
@Component
public class CommunityReviewAssessmentAttemptScheduler {

    private final AssessmentAttemptGateway assessmentAttemptGateway;
    private final EvaluateCommunityReviewAssessmentAttemptUseCase evaluateCommunityReviewAssessmentAttemptUseCase;

    public CommunityReviewAssessmentAttemptScheduler(
            AssessmentAttemptGateway assessmentAttemptGateway,
            EvaluateCommunityReviewAssessmentAttemptUseCase evaluateCommunityReviewAssessmentAttemptUseCase
    ) {
        this.assessmentAttemptGateway = assessmentAttemptGateway;
        this.evaluateCommunityReviewAssessmentAttemptUseCase = evaluateCommunityReviewAssessmentAttemptUseCase;
    }

    // Runs every 5 minutes and evaluates one COMPLETED system mini-project attempt per run.
    @Scheduled(fixedRate = 300000)
    public void evaluateExpiredCommunityReviewAttempts() {
        List<AssessmentAttempt> eligibleAttempts = assessmentAttemptGateway
                .findCompletedSystemProjectAttemptsReadyForDemoCommunityDecision();

        if (!eligibleAttempts.isEmpty()) {
            evaluateCommunityReviewAssessmentAttemptUseCase.execute(eligibleAttempts.getFirst().getId());
        }
    }
}
