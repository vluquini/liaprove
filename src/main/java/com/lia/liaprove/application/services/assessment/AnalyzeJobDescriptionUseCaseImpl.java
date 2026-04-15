package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.ai.JobDescriptionAnalysisGateway;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.usecases.assessments.AnalyzeJobDescriptionUseCase;

public class AnalyzeJobDescriptionUseCaseImpl implements AnalyzeJobDescriptionUseCase {

    private final JobDescriptionAnalysisGateway jobDescriptionAnalysisGateway;

    public AnalyzeJobDescriptionUseCaseImpl(JobDescriptionAnalysisGateway jobDescriptionAnalysisGateway) {
        this.jobDescriptionAnalysisGateway = jobDescriptionAnalysisGateway;
    }

    @Override
    public JobDescriptionAnalysis execute(String jobDescription) {
        if (jobDescription == null || jobDescription.isBlank()) {
            throw new IllegalArgumentException("Job description must not be blank.");
        }

        JobDescriptionAnalysis analysis = jobDescriptionAnalysisGateway.analyze(jobDescription);
        if (analysis == null) {
            return new JobDescriptionAnalysis(jobDescription, null, null, null, null);
        }

        String normalizedJobDescription = analysis.getOriginalJobDescription() == null
                ? jobDescription
                : analysis.getOriginalJobDescription();

        return new JobDescriptionAnalysis(
                normalizedJobDescription,
                analysis.getSuggestedKnowledgeAreas(),
                analysis.getSuggestedHardSkills(),
                analysis.getSuggestedSoftSkills(),
                analysis.getSuggestedCriteriaWeights()
        );
    }
}
