package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;

public interface AnalyzeJobDescriptionUseCase {
    JobDescriptionAnalysis execute(String jobDescription);
}
