package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.ai.JobDescriptionAnalysisGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyzeJobDescriptionUseCaseImplTest {

    @Mock
    private JobDescriptionAnalysisGateway jobDescriptionAnalysisGateway;

    @InjectMocks
    private AnalyzeJobDescriptionUseCaseImpl useCase;

    @Test
    void shouldAnalyzeJobDescriptionAndNormalizeGatewayResponse() {
        String jobDescription = "Senior Java developer with Spring and PostgreSQL";
        JobDescriptionAnalysis gatewayAnalysis = mock(JobDescriptionAnalysis.class);

        when(gatewayAnalysis.getOriginalJobDescription()).thenReturn(jobDescription);
        when(gatewayAnalysis.getSuggestedKnowledgeAreas()).thenReturn(null);
        when(gatewayAnalysis.getSuggestedHardSkills()).thenReturn(null);
        when(gatewayAnalysis.getSuggestedSoftSkills()).thenReturn(null);
        when(gatewayAnalysis.getSuggestedCriteriaWeights()).thenReturn(null);
        when(jobDescriptionAnalysisGateway.analyze(jobDescription)).thenReturn(gatewayAnalysis);

        JobDescriptionAnalysis analysis = useCase.execute(jobDescription);

        verify(jobDescriptionAnalysisGateway).analyze(jobDescription);
        assertThat(analysis.getOriginalJobDescription()).isEqualTo(jobDescription);
        assertThat(analysis.getSuggestedKnowledgeAreas()).isEqualTo(Set.of());
        assertThat(analysis.getSuggestedHardSkills()).isEqualTo(List.of());
        assertThat(analysis.getSuggestedSoftSkills()).isEqualTo(List.of());
        assertThat(analysis.getSuggestedCriteriaWeights()).isEqualTo(AssessmentCriteriaWeights.defaultWeights());
    }

    @Test
    void shouldRejectBlankJobDescription() {
        assertThatThrownBy(() -> useCase.execute("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job description must not be blank.");

        verifyNoInteractions(jobDescriptionAnalysisGateway);
    }
}
