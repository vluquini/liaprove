package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.QuestionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttemptPreAnalysisTest {

    @Test
    void shouldRejectNullMetadata() {
        AttemptPreAnalysis.Analysis analysis = analysis();

        assertThatThrownBy(() -> new AttemptPreAnalysis(null, analysis))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("metadata must not be null");
    }

    @Test
    void shouldRejectNullAnalysis() {
        AttemptPreAnalysis.Metadata metadata = metadata();

        assertThatThrownBy(() -> new AttemptPreAnalysis(metadata, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("analysis must not be null");
    }

    @Test
    void shouldRejectInvalidMetadataFields() {
        assertThatThrownBy(() -> new AttemptPreAnalysis.Metadata(null, LocalDateTime.now(), List.of()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("attemptId must not be null");

        assertThatThrownBy(() -> new AttemptPreAnalysis.Metadata(UUID.randomUUID(), null, List.of()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("generatedAt must not be null");
    }

    @Test
    void shouldNormalizeNullIgnoredQuestionTypes() {
        AttemptPreAnalysis.Metadata metadata = new AttemptPreAnalysis.Metadata(UUID.randomUUID(), LocalDateTime.now(), null);

        assertThat(metadata.getIgnoredQuestionTypes()).isEmpty();
    }

    @Test
    void shouldRejectBlankAnalysisTexts() {
        assertThatThrownBy(() -> new AttemptPreAnalysis.Analysis(" ", List.of(), List.of(), "Explanation"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("summary must not be blank.");

        assertThatThrownBy(() -> new AttemptPreAnalysis.Analysis("Summary", List.of(), List.of(), " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("finalExplanation must not be blank.");
    }

    @Test
    void shouldNormalizeNullAnalysisCollections() {
        AttemptPreAnalysis.Analysis analysis = new AttemptPreAnalysis.Analysis("Summary", null, null, "Explanation");

        assertThat(analysis.getStrengths()).isEmpty();
        assertThat(analysis.getAttentionPoints()).isEmpty();
    }

    @Test
    void shouldProtectCollectionsFromExternalMutation() {
        List<QuestionType> ignoredTypes = new java.util.ArrayList<>(List.of(QuestionType.OPEN));
        AttemptPreAnalysis.Metadata metadata = new AttemptPreAnalysis.Metadata(UUID.randomUUID(), LocalDateTime.now(), ignoredTypes);

        ignoredTypes.add(QuestionType.MULTIPLE_CHOICE);

        assertThat(metadata.getIgnoredQuestionTypes()).containsExactly(QuestionType.OPEN);
    }

    private AttemptPreAnalysis.Metadata metadata() {
        return new AttemptPreAnalysis.Metadata(UUID.randomUUID(), LocalDateTime.now(), List.of(QuestionType.OPEN));
    }

    private AttemptPreAnalysis.Analysis analysis() {
        return new AttemptPreAnalysis.Analysis("Summary", List.of("Strength"), List.of("Attention"), "Explanation");
    }
}
