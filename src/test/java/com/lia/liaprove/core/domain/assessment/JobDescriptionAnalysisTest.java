package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.KnowledgeArea;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JobDescriptionAnalysisTest {

    @Test
    void shouldRejectBlankJobDescription() {
        assertThatThrownBy(() -> new JobDescriptionAnalysis(
                "   ",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                List.of("Java"),
                List.of("Communication"),
                AssessmentCriteriaWeights.defaultWeights()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job description must not be blank.");
    }

    @Test
    void shouldNormalizeNullCollectionsToEmpty() {
        JobDescriptionAnalysis analysis = new JobDescriptionAnalysis(
                "Senior Java developer",
                null,
                null,
                null,
                AssessmentCriteriaWeights.defaultWeights()
        );

        assertThat(analysis.getSuggestedKnowledgeAreas()).isEmpty();
        assertThat(analysis.getSuggestedHardSkills()).isEmpty();
        assertThat(analysis.getSuggestedSoftSkills()).isEmpty();
    }

    @Test
    void shouldDefaultCriteriaWeightsWhenNull() {
        JobDescriptionAnalysis analysis = new JobDescriptionAnalysis(
                "Senior Java developer",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                List.of("Java"),
                List.of("Communication"),
                null
        );

        assertThat(analysis.getSuggestedCriteriaWeights()).isEqualTo(AssessmentCriteriaWeights.defaultWeights());
    }
}
