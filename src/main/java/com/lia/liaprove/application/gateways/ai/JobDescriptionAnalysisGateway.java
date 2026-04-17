package com.lia.liaprove.application.gateways.ai;

import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;

public interface JobDescriptionAnalysisGateway {
    JobDescriptionAnalysis analyze(String jobDescription);
}
