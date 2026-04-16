package com.lia.liaprove.infrastructure.services.ai;

import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.infrastructure.dtos.ai.LlmJobDescriptionAnalysisOutput;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HttpQuestionPreAnalysisGatewayImplTest {

    @Test
    void shouldFallbackToDefaultWeightsWhenProviderReturnsInvalidTotal() throws Exception {
        LlmJobDescriptionAnalysisOutput output = new LlmJobDescriptionAnalysisOutput(
                "Senior backend engineer",
                List.of("SOFTWARE_DEVELOPMENT"),
                List.of("Java"),
                List.of("Communication"),
                50,
                30,
                10
        );

        AssessmentCriteriaWeights weights = invokeResolveSuggestedWeights(output);

        assertThat(weights).isEqualTo(AssessmentCriteriaWeights.defaultWeights());
    }

    private AssessmentCriteriaWeights invokeResolveSuggestedWeights(LlmJobDescriptionAnalysisOutput output) throws Exception {
        Method method = HttpQuestionPreAnalysisGatewayImpl.class
                .getDeclaredMethod("resolveSuggestedWeights", LlmJobDescriptionAnalysisOutput.class);
        method.setAccessible(true);

        try {
            return (AssessmentCriteriaWeights) method.invoke(null, output);
        } catch (InvocationTargetException ex) {
            throw (Exception) ex.getTargetException();
        }
    }
}
