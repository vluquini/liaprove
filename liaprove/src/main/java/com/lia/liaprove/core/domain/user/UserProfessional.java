package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.domain.assessment.Assessment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserProfessional extends User{

    public UserProfessional() {}

    public UserProfessional(UUID id, String name, String email, String password, String occupation, String bio, ExperienceLevel experienceLevel, UserRole role, Integer voteWeight, Integer totalAssessmentsTaken, List<Certificate> certificates, Float averageScore, LocalDateTime registrationDate, LocalDateTime lastLogin) {
        super(id, name, email, password, occupation, bio, experienceLevel, role, voteWeight, totalAssessmentsTaken, certificates, averageScore, registrationDate, lastLogin);
    }

    // ---------- Métodos de domínio  ----------

    /**
     * Atualiza o nível de experiência do profissional.
     * Mudança simples de estado — valida se nulo.
     */
    public void updateExperienceLevel(ExperienceLevel newLevel) {
        if (newLevel == null) {
            throw new IllegalArgumentException("newLevel must not be null");
        }
        // Só muda se diferente — evita gravações desnecessárias.
        if (!Objects.equals(this.getExperienceLevel(), newLevel)) {
            this.setExperienceLevel(newLevel);
        }
    }

    /**
     * Aceita (anexa) um certificado ao usuário.
     * package-private para forçar que apenas usecases/services do mesmo módulo chamem.
     * A emissão/validação do certificado será feita no Use Case correspondente.
     */
    void acceptCertificate(Certificate certificate) {
        // delega ao método do agregado (que garante invariantes)
        // assumimos que User possui addCertificate(package-private/public)
        this.addCertificate(certificate);
    }

    /**
     * Wrapper semântico para registrar resultado de avaliação.
     * Delegação direta para o comportamento genérico do agregado.
     */
    public void recordAssessmentResultAsProfessional(float score) {
        // validação leve
        if (score < 0.0f) {
            throw new IllegalArgumentException("score must be >= 0");
        }
        this.recordAssessmentResult(score);
    }

    /**
     * Atualiza dados de perfil do profissional.
     */
    public void updateProfile(String occupation, String bio) {
        if (occupation != null && !occupation.isBlank()) {
            this.setOccupation(occupation.trim());
        }
        if (bio != null) {
            this.setBio(bio.trim());
        }
    }

    /**
     * Verificação simples se o usuário é elegível a tentar uma avaliação.
     * NÃO substitui regras de negócio completas (por ex., limites de retake),
     * que devem existir no AssessmentService/usecase.
     */
    public boolean isEligibleForAssessment(Assessment assessment) {
        if (assessment == null) return false;
        // exemplo simples: se o usuário está registrado há pelo menos 1 minuto (evita bots)
        java.time.LocalDateTime reg = this.getRegistrationDate();
        if (reg == null) return true; // sem registro, devolve true (fallback)
        return reg.isBefore(java.time.LocalDateTime.now().minusMinutes(1));
    }
}
