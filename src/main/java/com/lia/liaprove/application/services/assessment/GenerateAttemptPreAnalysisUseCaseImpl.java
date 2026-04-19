package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisGateway;
import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisContext;
import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.AttemptPreAnalysis;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.assessment.AttemptPreAnalysisInProgressException;
import com.lia.liaprove.core.exceptions.assessment.AttemptPreAnalysisNotAvailableException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.GenerateAttemptPreAnalysisUseCase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GenerateAttemptPreAnalysisUseCaseImpl implements GenerateAttemptPreAnalysisUseCase {

    private final AssessmentAttemptGateway attemptGateway;
    private final UserGateway userGateway;
    private final AttemptPreAnalysisGateway attemptPreAnalysisGateway;
    private final Set<UUID> inProgressAttemptIds = ConcurrentHashMap.newKeySet();

    public GenerateAttemptPreAnalysisUseCaseImpl(
            AssessmentAttemptGateway attemptGateway,
            UserGateway userGateway,
            AttemptPreAnalysisGateway attemptPreAnalysisGateway
    ) {
        this.attemptGateway = Objects.requireNonNull(attemptGateway, "attemptGateway must not be null");
        this.userGateway = Objects.requireNonNull(userGateway, "userGateway must not be null");
        this.attemptPreAnalysisGateway = Objects.requireNonNull(attemptPreAnalysisGateway, "attemptPreAnalysisGateway must not be null");
    }

    @Override
    public AttemptPreAnalysis execute(UUID attemptId, UUID requesterId) {
        AssessmentAttempt attempt = attemptGateway.findByIdWithCreator(attemptId)
                .orElseThrow(() -> new AssessmentNotFoundException("Evaluation attempt with ID " + attemptId + " not found."));

        User requester = userGateway.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Requesting user not found."));

        validateAuthorization(requester, attempt);

        // Múltiplas gerações continuam permitidas ao longo do tempo; apenas a concorrência simultânea é bloqueada.
        if (!inProgressAttemptIds.add(attemptId)) {
            throw new AttemptPreAnalysisInProgressException("Pre-analysis generation is already in progress for attempt " + attemptId + ".");
        }

        try {
            List<Question> supportedQuestions = filterSupportedQuestions(attempt.getQuestions());
            List<QuestionType> ignoredQuestionTypes = collectIgnoredQuestionTypes(attempt.getQuestions());

            if (supportedQuestions.isEmpty()) {
                throw new AttemptPreAnalysisNotAvailableException("Attempt " + attemptId + " does not contain supported questions for pre-analysis.");
            }

            AttemptPreAnalysisContext context = buildContext(attempt, supportedQuestions, ignoredQuestionTypes);
            AttemptPreAnalysis.Analysis analysis = attemptPreAnalysisGateway.generate(context);
            AttemptPreAnalysis.Metadata metadata = new AttemptPreAnalysis.Metadata(
                    attemptId,
                    LocalDateTime.now(),
                    ignoredQuestionTypes
            );
            return new AttemptPreAnalysis(metadata, analysis);
        } finally {
            inProgressAttemptIds.remove(attemptId);
        }
    }

    private void validateAuthorization(User requester, AssessmentAttempt attempt) {
        if (requester.getRole() == UserRole.ADMIN) {
            return;
        }

        Assessment assessment = attempt.getAssessment();
        if (!(assessment instanceof PersonalizedAssessment personalizedAssessment)) {
            throw new AuthorizationException("Access denied. This attempt does not belong to a personalized assessment.");
        }

        if (personalizedAssessment.getCreatedBy() == null || !personalizedAssessment.getCreatedBy().getId().equals(requester.getId())) {
            throw new AuthorizationException("You do not have permission to view the details of this attempt.");
        }
    }

    private List<Question> filterSupportedQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return List.of();
        }

        List<Question> supportedQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (question == null) {
                continue;
            }

            QuestionType questionType = question.getQuestionType();
            if (questionType == QuestionType.MULTIPLE_CHOICE || questionType == QuestionType.OPEN) {
                supportedQuestions.add(question);
            }
        }
        return List.copyOf(supportedQuestions);
    }

    private List<QuestionType> collectIgnoredQuestionTypes(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<QuestionType> ignoredTypes = new LinkedHashSet<>();
        for (Question question : questions) {
            if (question != null && question.getQuestionType() == QuestionType.PROJECT) {
                ignoredTypes.add(QuestionType.PROJECT);
            }
        }
        return List.copyOf(ignoredTypes);
    }

    private AttemptPreAnalysisContext buildContext(
            AssessmentAttempt attempt,
            List<Question> supportedQuestions,
            List<QuestionType> ignoredQuestionTypes) {
        Assessment assessment = attempt.getAssessment();
        AttemptPreAnalysisContext.AssessmentContext assessmentContext = buildAssessmentContext(assessment);
        AttemptPreAnalysisContext.CandidateContext candidateContext = buildCandidateContext(attempt.getUser());
        List<AttemptPreAnalysisContext.QuestionContext> questionContexts = mapQuestionContexts(supportedQuestions, attempt.getAnswers());

        return new AttemptPreAnalysisContext(
                attempt.getId(),
                attempt.getStatus(),
                attempt.getAccuracyRate(),
                assessmentContext,
                candidateContext,
                questionContexts,
                ignoredQuestionTypes
        );
    }

    private AttemptPreAnalysisContext.AssessmentContext buildAssessmentContext(Assessment assessment) {
        if (assessment instanceof PersonalizedAssessment personalizedAssessment) {
            return new AttemptPreAnalysisContext.AssessmentContext(
                    personalizedAssessment.getTitle(),
                    personalizedAssessment.getDescription(),
                    personalizedAssessment.getCriteriaWeights(),
                    personalizedAssessment.getJobDescriptionAnalysis()
            );
        }

        return new AttemptPreAnalysisContext.AssessmentContext(
                assessment == null ? null : assessment.getTitle(),
                assessment == null ? null : assessment.getDescription(),
                AssessmentCriteriaWeights.defaultWeights(),
                null
        );
    }

    private AttemptPreAnalysisContext.CandidateContext buildCandidateContext(User candidate) {
        if (candidate instanceof UserProfessional professional) {
            return new AttemptPreAnalysisContext.CandidateContext(
                    professional.getExperienceLevel(),
                    professional.getHardSkills(),
                    professional.getSoftSkills()
            );
        }

        return null;
    }

    private List<AttemptPreAnalysisContext.QuestionContext> mapQuestionContexts(
            List<Question> questions,
            List<Answer> answers) {
        if (questions == null || questions.isEmpty()) {
            return List.of();
        }

        Map<UUID, Answer> answersByQuestionId = indexAnswers(answers);
        List<AttemptPreAnalysisContext.QuestionContext> mapped = new ArrayList<>();

        for (Question question : questions) {
            if (question == null) {
                continue;
            }

            UUID questionId = question.getId();
            Answer answer = questionId == null ? null : answersByQuestionId.get(questionId);
            mapped.add(mapQuestionContext(question, answer));
        }

        return List.copyOf(mapped);
    }

    private AttemptPreAnalysisContext.QuestionContext mapQuestionContext(Question question, Answer answer) {
        if (question instanceof MultipleChoiceQuestion multipleChoiceQuestion) {
            return mapMultipleChoiceQuestion(multipleChoiceQuestion, answer);
        }

        if (question instanceof OpenQuestion openQuestion) {
            return mapOpenQuestion(openQuestion, answer);
        }

        return new AttemptPreAnalysisContext.QuestionContext(
                question.getId(),
                question.getQuestionType(),
                question.getTitle(),
                question.getDescription(),
                null,
                null,
                List.of(),
                null,
                null,
                null
        );
    }

    private AttemptPreAnalysisContext.QuestionContext mapMultipleChoiceQuestion(MultipleChoiceQuestion question, Answer answer) {
        List<AttemptPreAnalysisContext.AlternativeContext> alternatives = question.getAlternatives() == null
                ? List.of()
                : question.getAlternatives().stream()
                .filter(Objects::nonNull)
                .map(AttemptPreAnalysisContext.AlternativeContext::new)
                .toList();
        UUID selectedAlternativeId = answer == null ? null : answer.getSelectedAlternativeId();

        return new AttemptPreAnalysisContext.QuestionContext(
                question.getId(),
                question.getQuestionType(),
                question.getTitle(),
                question.getDescription(),
                null,
                null,
                alternatives,
                selectedAlternativeId,
                resolveSelectedAlternativeText(alternatives, selectedAlternativeId),
                null
        );
    }

    private AttemptPreAnalysisContext.QuestionContext mapOpenQuestion(OpenQuestion question, Answer answer) {
        return new AttemptPreAnalysisContext.QuestionContext(
                question.getId(),
                question.getQuestionType(),
                question.getTitle(),
                question.getDescription(),
                question.getGuideline(),
                question.getVisibility() == null ? null : question.getVisibility().name(),
                List.of(),
                null,
                null,
                answer == null ? null : answer.getTextResponse()
        );
    }

    private Map<UUID, Answer> indexAnswers(List<Answer> answers) {
        if (answers == null || answers.isEmpty()) {
            return Map.of();
        }

        Map<UUID, Answer> indexed = new HashMap<>();
        for (Answer answer : answers) {
            if (answer == null || answer.getQuestionId() == null) {
                continue;
            }
            indexed.put(answer.getQuestionId(), answer);
        }
        return indexed;
    }

    private String resolveSelectedAlternativeText(
            List<AttemptPreAnalysisContext.AlternativeContext> alternatives,
            UUID selectedAlternativeId) {
        if (alternatives == null || alternatives.isEmpty() || selectedAlternativeId == null) {
            return null;
        }

        for (AttemptPreAnalysisContext.AlternativeContext alternative : alternatives) {
            if (alternative != null && selectedAlternativeId.equals(alternative.alternativeId())) {
                return alternative.text();
            }
        }

        return null;
    }
}
