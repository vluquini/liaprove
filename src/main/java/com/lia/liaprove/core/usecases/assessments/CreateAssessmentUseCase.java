package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.Assessment;

import java.util.UUID;

public interface CreateAssessmentUseCase {
    // Usar DTO
    UUID createAssessment(Assessment dto, UUID adminId);
}

