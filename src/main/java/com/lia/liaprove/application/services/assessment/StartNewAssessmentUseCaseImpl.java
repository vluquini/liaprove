package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.*;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotActiveException;
import com.lia.liaprove.core.exceptions.assessment.MaxAttemptsReachedException;
import com.lia.liaprove.core.exceptions.assessment.UserAlreadyAttemptedAssessmentException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.StartNewAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.GenerateSystemAssessmentUseCase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Implementação do caso de uso responsável por iniciar uma nova tentativa de avaliação.
 * Este serviço gerencia o início da execução (do sistema ou personalizada), mas não cria a definição da avaliação.
 */
public class StartNewAssessmentUseCaseImpl implements StartNewAssessmentUseCase {

    private final AssessmentGateway assessmentGateway;
    private final AssessmentAttemptGateway attemptGateway;
    private final UserGateway userGateway;
    private final GenerateSystemAssessmentUseCase generateSystemAssessmentUseCase;

    public StartNewAssessmentUseCaseImpl(AssessmentGateway assessmentGateway, AssessmentAttemptGateway attemptGateway,
                                         UserGateway userGateway, GenerateSystemAssessmentUseCase generateSystemAssessmentUseCase) {
        this.assessmentGateway = assessmentGateway;
        this.attemptGateway = attemptGateway;
        this.userGateway = userGateway;
        this.generateSystemAssessmentUseCase = generateSystemAssessmentUseCase;
    }

    @Override
    public AssessmentAttempt execute(UUID userId, String shareableToken, Set<KnowledgeArea> knowledgeAreas,
                                     DifficultyLevel difficultyLevel) {
        User user = userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (shareableToken != null && !shareableToken.isBlank()) {
            return startPersonalizedAssessment(user, shareableToken);
        }

        if (knowledgeAreas != null && !knowledgeAreas.isEmpty() && difficultyLevel != null) {
            return startSystemAssessment(user, knowledgeAreas, difficultyLevel);
        } else {
            throw new IllegalArgumentException("Invalid criteria to start the assessment. Provide a token or area of knowledge and difficulty.");
        }
    }

    private AssessmentAttempt startPersonalizedAssessment(User user, String shareableToken) {
        PersonalizedAssessment assessment = (PersonalizedAssessment) assessmentGateway.findByShareableToken(shareableToken)
                .orElseThrow(() -> new AssessmentNotFoundException("Personalized assessment not found."));

        // Validações
        if (assessment.getStatus() != PersonalizedAssessmentStatus.ACTIVE) {
            throw new AssessmentNotActiveException("This assessment is not active.");
        }
        if (assessment.getExpirationDate() != null && LocalDateTime.now().isAfter(assessment.getExpirationDate())) {
            throw new AssessmentNotActiveException("This assessment has already expired.");
        }
        if (attemptGateway.existsByAssessmentIdAndUserId(assessment.getId(), user.getId())) {
            throw new UserAlreadyAttemptedAssessmentException("You have already completed this assessment.");
        }

        long attemptCount = attemptGateway.countByAssessmentId(assessment.getId());

        if (attemptCount >= assessment.getMaxAttempts()) {
            throw new MaxAttemptsReachedException("The maximum number of participants for this assessment has been reached.");
        }

        List<Question> questions = new ArrayList<>(assessment.getQuestions());
        Collections.shuffle(questions);

        AssessmentAttempt attempt = createAssessmentAttempt(assessment, user, questions);

        return attemptGateway.save(attempt);
    }

    private AssessmentAttempt startSystemAssessment(User user, Set<KnowledgeArea> knowledgeAreas, DifficultyLevel difficultyLevel) {
        List<Question> questions = generateSystemAssessmentUseCase.createQuestions(knowledgeAreas, difficultyLevel);
        if (questions.isEmpty()) {
            throw new AssessmentNotFoundException("It was not possible to generate an assessment. There are not enough questions for the selected criteria.");
        }

        SystemAssessment assessment = createSystemAssessment(knowledgeAreas, difficultyLevel, questions);

        AssessmentAttempt attempt = createAssessmentAttempt(assessment, user, questions);

        return attemptGateway.save(attempt);
    }

    private SystemAssessment createSystemAssessment(Set<KnowledgeArea> knowledgeAreas, DifficultyLevel difficultyLevel, List<Question> questions) {
        return new SystemAssessment(
                null,             // Null pois JPA gera na camada infra
                "Avaliação de " + knowledgeAreas.iterator().next().toString(),
                "Avaliação gerada pelo sistema.",
                LocalDateTime.now(),
                questions,
                // Lógica de tempo pode ser configurada aqui
                getTimerForDifficulty(difficultyLevel)
        );
    }

    private AssessmentAttempt createAssessmentAttempt(Assessment assessment, User user, List<Question> questions) {
        return new AssessmentAttempt(
                null,             // Null pois JPA gera na camada infra
                assessment,
                user,
                questions,           // A mesma lista, já embaralhada pela factory
                new ArrayList<>(),   // Answers
                new ArrayList<>(),   // Feedbacks
                LocalDateTime.now(),
                null,
                null,
                null,
                AssessmentAttemptStatus.IN_PROGRESS
        );
    }

    private Duration getTimerForDifficulty(DifficultyLevel difficultyLevel) {
        return switch (difficultyLevel) {
            case EASY   -> Duration.ofMinutes(5);
            case MEDIUM -> Duration.ofMinutes(10);
            default     -> Duration.ofMinutes(20);
        };
    }
}
