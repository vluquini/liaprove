package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Certificate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa a entidade base para todos os usuários do sistema, contendo atributos e comportamentos comuns.
 * Esta classe abstrata é estendida por tipos de usuários específicos, como UserProfessional e UserRecruiter.
 */
public abstract class User {
    private UUID id;
    private String name;
    private String email;
    private String passwordHash;
    // Título da profissão que o usuário exerce
    private String occupation;
    private String bio;
    private ExperienceLevel experienceLevel;
    private UserRole role;
    // Utilizado para medir o peso do voto nas questões
    private Integer voteWeight;
    private Integer totalAssessmentsTaken;
    private List<Certificate> certificates;
    // Média de pontuação nas avaliações realizadas
    private Float averageScore;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLogin;
    private UserStatus status = UserStatus.ACTIVE;

    public User(){}

    public User(UUID id, String name, String email, String passwordHash, String occupation, String bio,
                ExperienceLevel experienceLevel, UserRole role, Integer voteWeight, Integer totalAssessmentsTaken,
                List<Certificate> certificates, Float averageScore, LocalDateTime registrationDate,
                LocalDateTime lastLogin, UserStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.occupation = occupation;
        this.bio = bio;
        this.experienceLevel = experienceLevel;
        this.role = role;
        this.voteWeight = voteWeight;
        this.totalAssessmentsTaken = totalAssessmentsTaken;
        this.certificates = certificates;
        this.averageScore = averageScore;
        this.registrationDate = registrationDate;
        this.lastLogin = lastLogin;
        this.status = UserStatus.ACTIVE;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Define a senha já hashada. A aplicação deve empregar hashing/algoritmos de password
     * na camada de infra/security antes de chamar este setter.
     */
    public void setPasswordHash(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("hashedPassword must not be null/blank");
        }
        this.passwordHash = hashedPassword;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Integer getVoteWeight() {
        return voteWeight;
    }

    public void setVoteWeight(Integer voteWeight) {
        this.voteWeight = voteWeight;
    }

    public Integer getTotalAssessmentsTaken() {
        return totalAssessmentsTaken;
    }

    public void setTotalAssessmentsTaken(Integer totalAssessmentsTaken) {
        this.totalAssessmentsTaken = totalAssessmentsTaken;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public Float getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Float averageScore) {
        this.averageScore = averageScore;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    // Métodos de conveniência - Possivelmente necessários
    public boolean isRecruiter() {
        return this.role == UserRole.RECRUITER;
    }

    public boolean isProfessional() {
        return this.role == UserRole.PROFESSIONAL;
    }


    // ---------- Métodos de domínio ----------

    /**
     * Registra um novo certificado no histórico do usuário (evita duplicatas por id).
     */
    public void addCertificate(Certificate certificate) {
        Objects.requireNonNull(certificate, "certificate must not be null");
        if (this.certificates == null) {
            this.certificates = new ArrayList<>();
        }
        boolean exists = this.certificates.stream()
                .anyMatch(c -> Objects.equals(c.getCertificateNumber(), certificate.getCertificateNumber()));
        if (!exists) {
            this.certificates.add(certificate);
        }
    }

    /**
     * Remove um certificado pelo seu identificador (certificateNumber).
     */
    public boolean removeCertificate(String certificateNumber) {
        if (this.certificates == null || certificateNumber == null) return false;
        return this.certificates.removeIf(c -> certificateNumber.equals(c.getCertificateNumber()));
    }

    /**
     * Atualiza métricas do usuário após a conclusão de uma avaliação:
     * - incrementa totalAssessmentsTaken
     * - recalcula averageScore incrementalmente
     *
     * Fórmula numérica estável:
     * newAvg = (oldAvg * n + newScore) / (n + 1)
     */
    public void recordAssessmentResult(float newScore) {
        if (newScore < 0.0f) throw new IllegalArgumentException("score must be >= 0");
        int n = (this.totalAssessmentsTaken == null) ? 0 : this.totalAssessmentsTaken;
        float oldAvg = (this.averageScore == null) ? 0.0f : this.averageScore;
        float newAvg = (oldAvg * n + newScore) / (n + 1);
        this.totalAssessmentsTaken = n + 1;
        this.averageScore = newAvg;
    }

    /**
     * Incrementa apenas o contador de avaliações realizadas (util quando não há score, por ex. registro).
     */
    public void incrementAssessmentsTaken() {
        int n = (this.totalAssessmentsTaken == null) ? 0 : this.totalAssessmentsTaken;
        this.totalAssessmentsTaken = n + 1;
    }

    /**
     * Atualiza lastLogin para o horário atual.
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    /**
     * Atualiza o nível de experiência do usuário.
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
     * Define o voteWeight garantindo que o valor esteja entre os limites fornecidos.
     *
     * @param newWeight novo peso desejado
     * @param min menor valor permitido (inclusivo)
     * @param max maior valor permitido (inclusivo)
     * @throws IllegalArgumentException se min > max ou newWeight fora do intervalo
     */
    public void setVoteWeightSafely(int newWeight, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max");
        }
        if (newWeight < min || newWeight > max) {
            throw new IllegalArgumentException("voteWeight must be between " + min + " and " + max);
        }
        this.voteWeight = newWeight;
    }

    /**
     * Ajusta incrementalmente o voteWeight (delta pode ser positivo ou negativo),
     * saturando (clamping) entre os limites fornecidos.
     *
     * @param delta incremento (positivo) ou decremento (negativo)
     * @param min menor valor permitido (inclusivo)
     * @param max maior valor permitido (inclusivo)
     */
    public void adjustVoteWeight(int delta, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max");
        }
        int current = (this.voteWeight == null) ? min : this.voteWeight;
        int updated = Math.max(min, Math.min(max, current + delta));
        this.voteWeight = updated;
    }

    /**
     * Atualiza os campos do perfil do usuário de uma só vez.
     * Apenas os campos não nulos serão considerados para atualização.
     *
     * @param occupation A nova ocupação (se não for nulo).
     * @param bio A nova biografia (se não for nulo).
     * @param experienceLevel O novo nível de experiência (se não for nulo).
     */
    public void updateProfile(String name, String email, String occupation, String bio, ExperienceLevel experienceLevel) {
        if (name != null && !name.isBlank()) {
            this.setName(name.trim());
        }
        if (email != null && !email.isBlank()) {
            this.setEmail(email.trim());
        }
        if (occupation != null && !occupation.isBlank()) {
            this.setOccupation(occupation.trim());
        }
        if (bio != null) {
            this.setBio(bio.trim());
        }
        if (experienceLevel != null) {
            this.updateExperienceLevel(experienceLevel);
        }
    }
}
