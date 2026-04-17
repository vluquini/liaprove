package com.lia.liaprove.application.gateways.ai;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.user.ExperienceLevel;

import java.util.List;
import java.util.UUID;

public record AttemptPreAnalysisContext(
        UUID attemptId,
        AssessmentAttemptStatus attemptStatus,
        Integer accuracyRate,
        AssessmentContext assessment,
        CandidateContext candidate,
        List<QuestionContext> supportedQuestions,
        List<QuestionType> ignoredQuestionTypes
) {
    public AttemptPreAnalysisContext {
        supportedQuestions = supportedQuestions == null ? List.of() : List.copyOf(supportedQuestions);
        ignoredQuestionTypes = ignoredQuestionTypes == null ? List.of() : List.copyOf(ignoredQuestionTypes);
    }

    public record AssessmentContext(
            String title,
            String description,
            AssessmentCriteriaWeights criteriaWeights,
            JobDescriptionAnalysis jobDescriptionAnalysis
    ) {
    }

    public record CandidateContext(
            ExperienceLevel experienceLevel,
            List<String> hardSkills,
            List<String> softSkills
    ) {
        public CandidateContext {
            hardSkills = hardSkills == null ? List.of() : List.copyOf(hardSkills);
            softSkills = softSkills == null ? List.of() : List.copyOf(softSkills);
        }
    }

    public record QuestionContext(
            UUID questionId,
            QuestionType questionType,
            String title,
            String description,
            String guideline,
            String visibility,
            List<AlternativeContext> alternatives,
            UUID selectedAlternativeId,
            String selectedAlternativeText,
            String textResponse
    ) {
        public QuestionContext {
            alternatives = alternatives == null ? List.of() : List.copyOf(alternatives);
        }
    }

    public record AlternativeContext(
            UUID alternativeId,
            String text
    ) {
        public AlternativeContext(Alternative alternative) {
            this(alternative == null ? null : alternative.id(), alternative == null ? null : alternative.text());
        }
    }
}
