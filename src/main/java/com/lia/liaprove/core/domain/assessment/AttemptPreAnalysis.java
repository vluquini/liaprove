package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.QuestionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AttemptPreAnalysis {
    private final Metadata metadata;
    private final Analysis analysis;

    public AttemptPreAnalysis(Metadata metadata, Analysis analysis) {
        this.metadata = Objects.requireNonNull(metadata, "metadata must not be null");
        this.analysis = Objects.requireNonNull(analysis, "analysis must not be null");
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public static final class Metadata {
        private final UUID attemptId;
        private final LocalDateTime generatedAt;
        private final List<QuestionType> ignoredQuestionTypes;

        public Metadata(UUID attemptId, LocalDateTime generatedAt, List<QuestionType> ignoredQuestionTypes) {
            this.attemptId = Objects.requireNonNull(attemptId, "attemptId must not be null");
            this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt must not be null");
            this.ignoredQuestionTypes = ignoredQuestionTypes == null ? List.of() : List.copyOf(ignoredQuestionTypes);
        }

        public UUID getAttemptId() {
            return attemptId;
        }

        public LocalDateTime getGeneratedAt() {
            return generatedAt;
        }

        public List<QuestionType> getIgnoredQuestionTypes() {
            return ignoredQuestionTypes;
        }
    }

    public static final class Analysis {
        private final String summary;
        private final List<String> strengths;
        private final List<String> attentionPoints;
        private final String finalExplanation;

        public Analysis(String summary, List<String> strengths, List<String> attentionPoints, String finalExplanation) {
            if (summary == null || summary.isBlank()) {
                throw new IllegalArgumentException("summary must not be blank.");
            }
            if (finalExplanation == null || finalExplanation.isBlank()) {
                throw new IllegalArgumentException("finalExplanation must not be blank.");
            }
            this.summary = summary;
            this.strengths = strengths == null ? List.of() : List.copyOf(strengths);
            this.attentionPoints = attentionPoints == null ? List.of() : List.copyOf(attentionPoints);
            this.finalExplanation = finalExplanation;
        }

        public String getSummary() {
            return summary;
        }

        public List<String> getStrengths() {
            return strengths;
        }

        public List<String> getAttentionPoints() {
            return attentionPoints;
        }

        public String getFinalExplanation() {
            return finalExplanation;
        }
    }
}
