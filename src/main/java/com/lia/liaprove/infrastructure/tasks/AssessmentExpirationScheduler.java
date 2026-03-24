package com.lia.liaprove.infrastructure.tasks;

import com.lia.liaprove.core.usecases.assessments.UpdateExpiredAssessmentsStatusUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler para expirar avaliações personalizadas vencidas.
 */
@Component
public class AssessmentExpirationScheduler {

    private final UpdateExpiredAssessmentsStatusUseCase updateExpiredAssessmentsStatusUseCase;

    public AssessmentExpirationScheduler(UpdateExpiredAssessmentsStatusUseCase updateExpiredAssessmentsStatusUseCase) {
        this.updateExpiredAssessmentsStatusUseCase = updateExpiredAssessmentsStatusUseCase;
    }

    // Runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void expireAssessments() {
        updateExpiredAssessmentsStatusUseCase.execute();
    }
}
