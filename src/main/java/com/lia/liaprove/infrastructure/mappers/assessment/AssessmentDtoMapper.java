package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentAttemptDetailsResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentAttemptResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentAttemptSummaryResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentExplainabilityResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentResultResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentCriteriaWeightsResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.DeletePersonalizedAssessmentResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.EvaluateAssessmentAttemptResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.JobDescriptionAnalysisResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.PersonalizedAssessmentResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.ScoredQuestionResponse;
import com.lia.liaprove.infrastructure.dtos.assessment.UpdatePersonalizedAssessmentResponse;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
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
    @Mapping(target = "criteriaWeights", expression = "java(toCriteriaWeightsResponse(assessment.getCriteriaWeights()))")
    @Mapping(target = "jobDescriptionAnalysis", expression = "java(toJobDescriptionAnalysisResponse(assessment.getJobDescriptionAnalysis()))")
    PersonalizedAssessmentResponse toPersonalizedResponse(PersonalizedAssessment assessment);

    default EvaluateAssessmentAttemptResponse toEvaluateAttemptResponse(AssessmentAttempt attempt) {
        if (attempt == null) {
            return null;
        }

        Assessment assessment = attempt.getAssessment();
        PersonalizedAssessment personalizedAssessment =
                assessment instanceof PersonalizedAssessment pa ? pa : null;
        AssessmentAttemptSummaryResponse.AssessmentSummary assessmentSummary =
                new AssessmentAttemptSummaryResponse.AssessmentSummary(
                        assessment != null ? assessment.getId() : null,
                        assessment != null ? assessment.getTitle() : null,
                        assessment instanceof PersonalizedAssessment,
                        personalizedAssessment != null
                                ? toCriteriaWeightsResponse(personalizedAssessment.getCriteriaWeights())
                                : null,
                        personalizedAssessment != null
                                ? toJobDescriptionAnalysisResponse(personalizedAssessment.getJobDescriptionAnalysis())
                                : null
                );

        return new EvaluateAssessmentAttemptResponse(
                attempt.getId(),
                attempt.getStatus(),
                attempt.getAccuracyRate(),
                assessmentSummary,
                toUserResponseDto(attempt.getUser()),
                toExplainabilityResponse(attempt),
                "Assessment evaluated successfully."
        );
    }

    default AssessmentAttemptDetailsResponse toAttemptDetailsResponse(AssessmentAttempt attempt) {
        if (attempt == null) {
            return null;
        }

        Assessment assessment = attempt.getAssessment();
        PersonalizedAssessment personalizedAssessment =
                assessment instanceof PersonalizedAssessment pa ? pa : null;
        AssessmentAttemptDetailsResponse.AssessmentSummary assessmentSummary =
                new AssessmentAttemptDetailsResponse.AssessmentSummary(
                        assessment != null ? assessment.getId() : null,
                        assessment != null ? assessment.getTitle() : null,
                        assessment != null ? assessment.getDescription() : null,
                        assessment != null && assessment.getEvaluationTimer() != null
                                ? assessment.getEvaluationTimer().toMinutes()
                                : null,
                        personalizedAssessment != null
                                ? toCriteriaWeightsResponse(personalizedAssessment.getCriteriaWeights())
                                : null,
                        personalizedAssessment != null
                                ? toJobDescriptionAnalysisResponse(personalizedAssessment.getJobDescriptionAnalysis())
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
                toExplainabilityResponse(attempt),
                questions
        );
    }

    default AssessmentAttemptSummaryResponse toAttemptSummaryResponse(AssessmentAttempt attempt) {
        if (attempt == null) {
            return null;
        }

        Assessment assessment = attempt.getAssessment();
        PersonalizedAssessment personalizedAssessment =
                assessment instanceof PersonalizedAssessment pa ? pa : null;
        AssessmentAttemptSummaryResponse.AssessmentSummary assessmentSummary =
                new AssessmentAttemptSummaryResponse.AssessmentSummary(
                        assessment != null ? assessment.getId() : null,
                        assessment != null ? assessment.getTitle() : null,
                        assessment instanceof PersonalizedAssessment,
                        personalizedAssessment != null
                                ? toCriteriaWeightsResponse(personalizedAssessment.getCriteriaWeights())
                                : null,
                        personalizedAssessment != null
                                ? toJobDescriptionAnalysisResponse(personalizedAssessment.getJobDescriptionAnalysis())
                                : null
                );

        return new AssessmentAttemptSummaryResponse(
                attempt.getId(),
                attempt.getStatus(),
                attempt.getAccuracyRate(),
                attempt.getStartedAt(),
                attempt.getFinishedAt(),
                assessmentSummary,
                toUserResponseDto(attempt.getUser())
        );
    }

    default UpdatePersonalizedAssessmentResponse toUpdatePersonalizedResponse(PersonalizedAssessment assessment) {
        if (assessment == null) {
            return null;
        }
        return new UpdatePersonalizedAssessmentResponse(
                assessment.getId(),
                assessment.getExpirationDate(),
                assessment.getMaxAttempts(),
                assessment.getStatus(),
                toCriteriaWeightsResponse(assessment.getCriteriaWeights()),
                toJobDescriptionAnalysisResponse(assessment.getJobDescriptionAnalysis())
        );
    }

    default JobDescriptionAnalysisResponse toJobDescriptionAnalysisResponse(JobDescriptionAnalysis analysis) {
        if (analysis == null) {
            return null;
        }

        return new JobDescriptionAnalysisResponse(
                analysis.getOriginalJobDescription(),
                analysis.getSuggestedKnowledgeAreas().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()),
                analysis.getSuggestedHardSkills(),
                analysis.getSuggestedSoftSkills(),
                toCriteriaWeightsResponse(analysis.getSuggestedCriteriaWeights())
        );
    }

    default AssessmentCriteriaWeightsResponse toCriteriaWeightsResponse(AssessmentCriteriaWeights weights) {
        if (weights == null) {
            return null;
        }

        return new AssessmentCriteriaWeightsResponse(
                weights.getHardSkillsWeight(),
                weights.getSoftSkillsWeight(),
                weights.getExperienceWeight()
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
        String guideline = null;
        if (question instanceof MultipleChoiceQuestion mcq) {
            alternatives = mcq.getAlternatives().stream()
                    .map(alt -> new AssessmentAttemptDetailsResponse.AlternativeResponse(alt.id(), alt.text()))
                    .collect(Collectors.toList());
        } else if (question instanceof OpenQuestion openQuestion) {
            guideline = openQuestion.getGuideline();
        }

        AssessmentAttemptDetailsResponse.AnswerResponse answerResponse = null;
        if (answer != null) {
            answerResponse = new AssessmentAttemptDetailsResponse.AnswerResponse(
                    answer.getQuestionId(),
                    answer.getSelectedAlternativeId(),
                    answer.getProjectUrl(),
                    answer.getTextResponse()
            );
        }

        return new AssessmentAttemptDetailsResponse.AttemptQuestionDetailsResponse(
                question.getId(),
                question.getTitle(),
                question.getDescription(),
                guideline,
                alternatives,
                answerResponse
        );
    }

    default AssessmentExplainabilityResponse toExplainabilityResponse(AssessmentAttempt attempt) {
        if (attempt == null) {
            return null;
        }

        List<Question> questions = attempt.getQuestions();
        List<Answer> answers = attempt.getAnswers();
        User user = attempt.getUser();
        UserProfessional professional = user instanceof UserProfessional up ? up : null;

        int totalQuestions = questions == null ? 0 : questions.size();
        int answeredQuestions = countAnsweredQuestions(answers);
        int multipleChoiceQuestions = countQuestionsByType(questions, QuestionType.MULTIPLE_CHOICE);
        int openQuestions = countQuestionsByType(questions, QuestionType.OPEN);
        int projectQuestions = countQuestionsByType(questions, QuestionType.PROJECT);

        String candidateExperienceLevel = professional != null && professional.getExperienceLevel() != null
                ? professional.getExperienceLevel().name()
                : null;

        List<String> candidateHardSkills = professional != null ? professional.getHardSkills() : null;
        List<String> candidateSoftSkills = professional != null ? professional.getSoftSkills() : null;
        AssessmentCriteriaWeightsResponse criteriaWeights = null;
        if (attempt.getAssessment() instanceof PersonalizedAssessment personalizedAssessment) {
            criteriaWeights = toCriteriaWeightsResponse(personalizedAssessment.getCriteriaWeights());
        }

        return new AssessmentExplainabilityResponse(
                totalQuestions,
                answeredQuestions,
                multipleChoiceQuestions,
                openQuestions,
                projectQuestions,
                candidateExperienceLevel,
                candidateHardSkills,
                candidateSoftSkills,
                criteriaWeights
        );
    }

    default int countQuestionsByType(List<Question> questions, QuestionType questionType) {
        if (questions == null || questionType == null) {
            return 0;
        }

        return (int) questions.stream()
                .filter(question -> question != null && question.getQuestionType() == questionType)
                .count();
    }

    default int countAnsweredQuestions(List<Answer> answers) {
        if (answers == null) {
            return 0;
        }

        return (int) answers.stream()
                .filter(this::hasProvidedAnswer)
                .map(Answer::getQuestionId)
                .filter(questionId -> questionId != null)
                .distinct()
                .count();
    }

    default boolean hasProvidedAnswer(Answer answer) {
        if (answer == null) {
            return false;
        }

        return answer.getSelectedAlternativeId() != null
                || hasText(answer.getProjectUrl())
                || hasText(answer.getTextResponse());
    }

    default boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
