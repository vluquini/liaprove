package com.lia.liaprove.core.domain.assessment;

import java.util.UUID;

/**
 * Representa a resposta de um usuário a uma única questão dentro de um AssessmentAttempt.
 */
public class Answer {
    private final UUID questionId;
    private UUID selectedAlternativeId; // Para questões de múltipla escolha
    private String projectUrl;          // Para mini-projetos

    public Answer(UUID questionId) {
        this.questionId = questionId;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public UUID getSelectedAlternativeId() {
        return selectedAlternativeId;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setSelectedAlternativeId(UUID selectedAlternativeId) {
        this.selectedAlternativeId = selectedAlternativeId;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }
}
