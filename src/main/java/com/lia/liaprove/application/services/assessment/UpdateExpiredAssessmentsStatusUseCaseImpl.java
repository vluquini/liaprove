package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.usecases.assessments.UpdateExpiredAssessmentsStatusUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementação do caso de uso para desativar avaliações expiradas.
 */
public class UpdateExpiredAssessmentsStatusUseCaseImpl implements UpdateExpiredAssessmentsStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateExpiredAssessmentsStatusUseCaseImpl.class);
    private final AssessmentGateway assessmentGateway;

    public UpdateExpiredAssessmentsStatusUseCaseImpl(AssessmentGateway assessmentGateway) {
        this.assessmentGateway = assessmentGateway;
    }

    @Override
    public void execute() {
        log.info("Running scheduled job to update expired assessments...");

        // 1. Buscar Candidatos
        List<PersonalizedAssessment> expiredAssessments = assessmentGateway.findActiveAssessmentsWithPastExpirationDate();

        if (expiredAssessments == null || expiredAssessments.isEmpty()) {
            log.info("No expired assessments found to update.");
            return;
        }

        log.info("Found {} expired assessments to deactivate.", expiredAssessments.size());

        // 2. Iterar e Atualizar o status
        for (PersonalizedAssessment assessment : expiredAssessments) {
            assessment.setStatus(PersonalizedAssessmentStatus.DEACTIVATED);
        }

        // 3. Persistir em Lote
        assessmentGateway.saveAll(expiredAssessments);

        log.info("Successfully deactivated {} expired assessments.", expiredAssessments.size());
    }
}
