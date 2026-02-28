package com.lia.liaprove.application.gateways.assessment;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentGateway {
    Optional<Assessment> findById(UUID id);
    Optional<Assessment> findByShareableToken(String token);
    Assessment save(Assessment assessment);

    /**
     * Salva uma lista de avaliações em lote.
     * @param assessments A lista de avaliações a serem salvas.
     */
    void saveAll(List<PersonalizedAssessment> assessments);

    void deletePersonalizedAssessmentById(UUID assessmentId);

    /**
     * Encontra todas as avaliações personalizadas que estão com status ATIVO,
     * mas cuja data de expiração já passou.
     * @return Uma lista de avaliações personalizadas expiradas.
     */
    List<PersonalizedAssessment> findActiveAssessmentsWithPastExpirationDate();
}

