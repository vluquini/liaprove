package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.domain.metrics.Feedback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public abstract class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    // Título da profissão que o usuário exerce
    private String occupation;
    private String bio;
    private ExperienceLevel experienceLevel;
    private UserRole role;
    // Utilizado para medir o peso do voto nas questões
    private Integer voteWeight;
    private Integer totalAssessmentsTaken;
    private List<Certificate> certificates;
    private List<Feedback> feedbacks;
    // Média de pontuação nas avaliações realizadas
    private Float averageScore;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLogin;

    public User(UUID id, String name, String email, String password, String occupation, String bio,
                ExperienceLevel experienceLevel, UserRole role, Integer voteWeight, Integer totalAssessmentsTaken,
                List<Certificate> certificates, List<Feedback> feedbacks, Float averageScore, LocalDateTime registrationDate,
                LocalDateTime lastLogin) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.password = password;
        this.occupation = occupation;
        this.bio = bio;
        this.experienceLevel = experienceLevel;
        this.role = role;
        this.voteWeight = voteWeight;
        this.totalAssessmentsTaken = totalAssessmentsTaken;
        this.certificates = certificates;
        this.feedbacks = feedbacks;
        this.averageScore = averageScore;
        this.registrationDate = registrationDate;
        this.lastLogin = lastLogin;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
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
}
