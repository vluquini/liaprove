package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.exceptions.AssessmentAttemptFinishedException;
import com.lia.liaprove.core.exceptions.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.AuthorizationException;
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

    public SubmitAssessmentUseCaseImpl(AssessmentAttemptGateway attemptGateway, IssueCertificateUseCase issueCertificateUseCase) {
        this.attemptGateway = attemptGateway;
        this.issueCertificateUseCase = issueCertificateUseCase;
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

        // 4. Validar Tempo Limite
        // Se o tempo expirou, permitimos a finalização com as respostas enviadas até o momento.
        // A lógica de pontuação na entidade tratará questões não respondidas como erradas.

        // 5. Converter DTO de respostas para Entidade de Domínio
        List<Answer> domainAnswers = new ArrayList<>();
        if (submissionDto.answers() != null) {
            domainAnswers = submissionDto.answers().stream()
                    .map(dto -> {
                        Answer answer = new Answer(dto.questionId());
                        answer.setSelectedAlternativeId(dto.selectedAlternativeId());
                        answer.setProjectUrl(dto.projectUrl());
                        return answer;
                    })
                    .collect(Collectors.toList());
        }

        // 6. Finalizar a tentativa (cálculo de nota e status)
        attempt.finish(domainAnswers);

        // Se o tempo expirou e o usuário tentou enviar depois, a lógica de finish processa normalmente.
        // O cálculo de nota já penaliza o que não foi respondido.
        
        // 7. Persistir tentativa finalizada
        AssessmentAttempt savedAttempt = attemptGateway.save(attempt);

        // 8. Emitir Certificado (Se aprovado)
        if (savedAttempt.getStatus() == AssessmentAttemptStatus.APPROVED) {
            Certificate certificate = issueCertificateUseCase.execute(savedAttempt);
            savedAttempt.setCertificate(certificate);
            // Salvar novamente para vincular o certificado
            savedAttempt = attemptGateway.save(savedAttempt);
        }

        return savedAttempt;
    }
}
