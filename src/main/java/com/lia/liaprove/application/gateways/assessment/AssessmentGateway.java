package com.lia.liaprove.application.gateways.assessment;

import com.lia.liaprove.core.domain.assessment.Assessment;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentGateway {
    Optional<Assessment> findById(UUID id);
}
