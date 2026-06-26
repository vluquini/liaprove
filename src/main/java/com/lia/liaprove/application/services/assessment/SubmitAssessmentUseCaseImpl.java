package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.application.services.assessment.dto.SubmitAssessmentAnswersDto;
import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.domain.assessment.SystemAssessment;
import com.lia.liaprove.core.exceptions.assessment.AssessmentAttemptFinishedException;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.usecases.assessments.IssueCertificateUseCase;
import com.lia.liaprove.core.usecases.assessments.SubmitAssessmentUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação do caso de uso para submeter respostas de uma avaliação.
 */
public class SubmitAssessmentUseCaseImpl implements SubmitAssessmentUseCase {

    private final AssessmentAttemptGateway attemptGateway;
    private final IssueCertificateUseCase issueCertificateUseCase;
    private final UserGateway userGateway;

    public SubmitAssessmentUseCaseImpl(AssessmentAttemptGateway attemptGateway,
                                       IssueCertificateUseCase issueCertificateUseCase,
                                       UserGateway userGateway) {
        this.attemptGateway = attemptGateway;
        this.issueCertificateUseCase = issueCertificateUseCase;
        this.userGateway = userGateway;
    }

    @Override
    public AssessmentAttempt execute(SubmitAssessmentAnswersDto submissionDto, UUID userId) {
        // 1. Buscar a tentativa
        AssessmentAttempt attempt = attemptGateway.findById(submissionDto.attemptId())
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment attempt not found."));

        // 2. Validar o usuário
        if (!attempt.getUser().getId().equals(userId)) {
            throw new AuthorizationException("This assessment attempt does not belong to the requesting user.");
        }

        // 3. Validar se já foi finalizada
        if (attempt.getStatus() != AssessmentAttemptStatus.IN_PROGRESS) {
            throw new AssessmentAttemptFinishedException("This assessment has already been completed.");
        }

        // 4. Converter DTO de respostas para Entidade de Domínio
        List<Answer> domainAnswers = new ArrayList<>();
        if (submissionDto.answers() != null) {
            domainAnswers = submissionDto.answers().stream()
                    .map(dto -> {
                        Answer answer = new Answer(dto.questionId());
                        answer.setSelectedAlternativeId(dto.selectedAlternativeId());
                        answer.setProjectUrl(dto.projectUrl());
                        answer.setTextResponse(dto.textResponse());
                        return answer;
                    })
                    .collect(Collectors.toList());
        }

        // 5. Finalizar a tentativa (cálculo de nota e status)
        attempt.finish(domainAnswers);

        // 6. Persistir tentativa finalizada
        AssessmentAttempt savedAttempt = attemptGateway.save(attempt);
        recordUserMetricsForFinalAttempt(savedAttempt);

        // 7. Emitir Certificado (Se aprovado)
        if (savedAttempt.getStatus() == AssessmentAttemptStatus.APPROVED && shouldIssueCertificate(savedAttempt)) {
            Certificate certificate = issueCertificateUseCase.execute(savedAttempt);
            savedAttempt.setCertificate(certificate);
            // Salvar novamente para vincular o certificado
            savedAttempt = attemptGateway.save(savedAttempt);
        }

        return savedAttempt;
    }

    private void recordUserMetricsForFinalAttempt(AssessmentAttempt attempt) {
        if (attempt.getAccuracyRate() == null) {
            return;
        }

        if (attempt.getStatus() != AssessmentAttemptStatus.APPROVED
                && attempt.getStatus() != AssessmentAttemptStatus.FAILED) {
            return;
        }

        attempt.getUser().recordAssessmentResult(attempt.getAccuracyRate());
        userGateway.save(attempt.getUser());
    }

    private boolean shouldIssueCertificate(AssessmentAttempt attempt) {
        if (!(attempt.getAssessment() instanceof SystemAssessment systemAssessment)) {
            return true;
        }

        if (systemAssessment.getKnowledgeArea() == null || systemAssessment.getDifficultyLevel() == null) {
            return true;
        }

        return attemptGateway.findBestCertifiedSystemAttemptByUserAndCriteria(
                        attempt.getUser().getId(),
                        systemAssessment.getKnowledgeArea(),
                        systemAssessment.getDifficultyLevel()
                )
                .map(existingAttempt -> isScoreImproved(attempt, existingAttempt))
                .orElse(true);
    }

    private boolean isScoreImproved(AssessmentAttempt attempt, AssessmentAttempt existingAttempt) {
        if (existingAttempt.getCertificate() == null || existingAttempt.getCertificate().getScore() == null) {
            return true;
        }

        if (attempt.getAccuracyRate() == null) {
            return true;
        }

        return attempt.getAccuracyRate() > existingAttempt.getCertificate().getScore();
    }
}
