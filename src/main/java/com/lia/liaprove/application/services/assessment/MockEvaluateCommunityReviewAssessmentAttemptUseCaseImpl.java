package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.SystemAssessment;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.assessment.InvalidAttemptStatusException;
import com.lia.liaprove.core.exceptions.user.InvalidUserDataException;
import com.lia.liaprove.core.usecases.assessments.EvaluateCommunityReviewAssessmentAttemptUseCase;

import java.util.Random;
import java.util.UUID;

/**
 * Implementação mock do fluxo de decisão comunitária de mini-projetos.
 * Existe por pragmatismo para apresentação do TCC até a integração da lógica
 * bayesiana real da plataforma.
 */
public class MockEvaluateCommunityReviewAssessmentAttemptUseCaseImpl implements EvaluateCommunityReviewAssessmentAttemptUseCase {

    private final AssessmentAttemptGateway assessmentAttemptGateway;
    private final Random random = new Random();

    public MockEvaluateCommunityReviewAssessmentAttemptUseCaseImpl(AssessmentAttemptGateway assessmentAttemptGateway) {
        this.assessmentAttemptGateway = assessmentAttemptGateway;
    }

    @Override
    public AssessmentAttempt execute(UUID attemptId) {
        AssessmentAttempt attempt = assessmentAttemptGateway.findById(attemptId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment attempt with id " + attemptId + " not found."));

        validateEligibility(attempt);

        if (random.nextBoolean()) {
            attempt.approve();
        } else {
            attempt.fail();
        }
        return assessmentAttemptGateway.save(attempt);
    }

    private void validateEligibility(AssessmentAttempt attempt) {
        if (attempt.getStatus() != AssessmentAttemptStatus.COMPLETED) {
            throw new InvalidAttemptStatusException("Only attempts with status COMPLETED can be community evaluated.");
        }

        if (!(attempt.getAssessment() instanceof SystemAssessment)) {
            throw new InvalidUserDataException("Only system mini-project attempts can be community evaluated.");
        }

        boolean hasProjectSubmission = attempt.getAnswers() != null
                && attempt.getAnswers().stream()
                .anyMatch(answer -> answer.getProjectUrl() != null && !answer.getProjectUrl().isBlank());

        if (!hasProjectSubmission) {
            throw new InvalidUserDataException("Only system mini-project attempts can be community evaluated.");
        }

        if (attempt.getFinishedAt() == null) {
            throw new InvalidUserDataException("Only finished attempts can be community evaluated.");
        }
    }
}
