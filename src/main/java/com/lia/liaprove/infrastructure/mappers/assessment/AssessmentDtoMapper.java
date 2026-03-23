package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentAttemptResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentResultResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.EvaluateAssessmentAttemptResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.PersonalizedAssessmentResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.ScoredQuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssessmentDtoMapper {

    @Mapping(target = "attemptId", source = "id")
    @Mapping(target = "assessmentTitle", source = "assessment.title")
    @Mapping(target = "evaluationTimerMinutes", expression = "java(attempt.getAssessment().getEvaluationTimer().toMinutes())")
    @Mapping(target = "questions", source = "questions", qualifiedByName = "mapQuestions")
    AssessmentAttemptResponse toAttemptResponse(AssessmentAttempt attempt);

    @Mapping(target = "certificateUrl", source = "certificate.certificateUrl")
    @Mapping(target = "message", constant = "Assessment submitted successfully.")
    AssessmentResultResponse toResultResponse(AssessmentAttempt attempt);

    @Mapping(target = "shareableToken", source = "shareableToken")
    @Mapping(target = "status", expression = "java(assessment.getStatus().name())")
    PersonalizedAssessmentResponse toPersonalizedResponse(PersonalizedAssessment assessment);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "accuracyRate", source = "accuracyRate")
    @Mapping(target = "message", constant = "Assessment evaluated successfully.")
    EvaluateAssessmentAttemptResponse toEvaluateAttemptResponse(AssessmentAttempt attempt);

    @Mapping(target = "id", source = "question.id")
    @Mapping(target = "title", source = "question.title")
    @Mapping(target = "description", source = "question.description")
    @Mapping(target = "knowledgeAreas", source = "question.knowledgeAreas")
    @Mapping(target = "difficultyLevel", source = "question.difficultyByCommunity")
    ScoredQuestionResponse toScoredQuestionResponse(ScoredQuestion scoredQuestion);

    @Named("mapQuestions")
    default List<AssessmentAttemptResponse.AttemptQuestionResponse> mapQuestions(List<Question> questions) {
        if (questions == null) return null;
        return questions.stream().map(this::mapQuestion).collect(Collectors.toList());
    }

    default AssessmentAttemptResponse.AttemptQuestionResponse mapQuestion(Question question) {
        if (question == null) return null;
        Object alternatives = null;
        if (question instanceof MultipleChoiceQuestion mcq) {
            alternatives = mcq.getAlternatives().stream()
                    .map(alt -> new AlternativeDto(alt.id(), alt.text())) // Using a simple internal record-like representation or just Map
                    .collect(Collectors.toList());
        }
        return new AssessmentAttemptResponse.AttemptQuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getDescription(),
                alternatives
        );
    }

    // Helper record for alternatives in response
    record AlternativeDto(UUID id, String text) {}
}
