package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.usecases.user.ListMyCertificatesUseCase;

import java.util.List;
import java.util.UUID;

public class ListMyCertificatesUseCaseImpl implements ListMyCertificatesUseCase {

    private final AssessmentAttemptGateway assessmentAttemptGateway;

    public ListMyCertificatesUseCaseImpl(AssessmentAttemptGateway assessmentAttemptGateway) {
        this.assessmentAttemptGateway = assessmentAttemptGateway;
    }

    @Override
    public List<Certificate> execute(UUID userId) {
        return assessmentAttemptGateway.findCertificatesByUserId(userId);
    }
}
