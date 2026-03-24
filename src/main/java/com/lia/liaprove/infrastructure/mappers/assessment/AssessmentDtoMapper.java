package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentAttemptDetailsResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentAttemptResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentResultResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.DeletePersonalizedAssessmentResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.EvaluateAssessmentAttemptResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.PersonalizedAssessmentResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.ScoredQuestionResponse;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;
import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
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

    default AssessmentAttemptDetailsResponse toAttemptDetailsResponse(AssessmentAttempt attempt) {
        if (attempt == null) {
            return null;
        }

        Assessment assessment = attempt.getAssessment();
        AssessmentAttemptDetailsResponse.AssessmentSummary assessmentSummary =
                new AssessmentAttemptDetailsResponse.AssessmentSummary(
                        assessment != null ? assessment.getId() : null,
                        assessment != null ? assessment.getTitle() : null,
                        assessment != null ? assessment.getDescription() : null,
                        assessment != null && assessment.getEvaluationTimer() != null
                                ? assessment.getEvaluationTimer().toMinutes()
                                : null
                );

        UserResponseDto candidate = toUserResponseDto(attempt.getUser());
        List<AssessmentAttemptDetailsResponse.AttemptQuestionDetailsResponse> questions =
                mapAttemptQuestions(attempt.getQuestions(), attempt.getAnswers());

        return new AssessmentAttemptDetailsResponse(
                attempt.getId(),
                attempt.getStatus(),
                attempt.getAccuracyRate(),
                attempt.getStartedAt(),
                attempt.getFinishedAt(),
                assessmentSummary,
                candidate,
                questions
        );
    }

    default DeletePersonalizedAssessmentResponse toDeleteResponse(UUID assessmentId) {
        return new DeletePersonalizedAssessmentResponse(
                assessmentId,
                "Assessment deleted successfully."
        );
    }

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

    UserResponseDto toUserResponseDto(User user);

    default List<AssessmentAttemptDetailsResponse.AttemptQuestionDetailsResponse> mapAttemptQuestions(
            List<Question> questions,
            List<Answer> answers
    ) {
        if (questions == null) {
            return null;
        }

        Map<UUID, Answer> answersByQuestionId = answers == null
                ? Map.of()
                : answers.stream().collect(Collectors.toMap(Answer::getQuestionId, Function.identity(), (a, b) -> a));

        return questions.stream()
                .map(question -> mapAttemptQuestion(question, answersByQuestionId.get(question.getId())))
                .collect(Collectors.toList());
    }

    default AssessmentAttemptDetailsResponse.AttemptQuestionDetailsResponse mapAttemptQuestion(
            Question question,
            Answer answer
    ) {
        if (question == null) {
            return null;
        }

        List<AssessmentAttemptDetailsResponse.AlternativeResponse> alternatives = null;
        if (question instanceof MultipleChoiceQuestion mcq) {
            alternatives = mcq.getAlternatives().stream()
                    .map(alt -> new AssessmentAttemptDetailsResponse.AlternativeResponse(alt.id(), alt.text()))
                    .collect(Collectors.toList());
        }

        AssessmentAttemptDetailsResponse.AnswerResponse answerResponse = null;
        if (answer != null) {
            answerResponse = new AssessmentAttemptDetailsResponse.AnswerResponse(
                    answer.getQuestionId(),
                    answer.getSelectedAlternativeId(),
                    answer.getProjectUrl()
            );
        }

        return new AssessmentAttemptDetailsResponse.AttemptQuestionDetailsResponse(
                question.getId(),
                question.getTitle(),
                question.getDescription(),
                alternatives,
                answerResponse
        );
    }
}
