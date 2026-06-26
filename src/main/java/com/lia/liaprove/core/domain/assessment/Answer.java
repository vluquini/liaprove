package com.lia.liaprove.core.domain.assessment;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa a resposta de um usuário a uma única questão dentro de um AssessmentAttempt.
 */
public class Answer {
    private final UUID questionId;
    private UUID selectedAlternativeId; // Para questões de múltipla escolha
    private String projectUrl;          // Para mini-projetos
    private String textResponse;        // Para questões abertas

    public Answer(UUID questionId) {
        this.questionId = Objects.requireNonNull(questionId, "questionId must not be null");
    }

    public static Answer multipleChoice(UUID questionId, UUID selectedAlternativeId) {
        Answer answer = new Answer(questionId);
        answer.setSelectedAlternativeId(selectedAlternativeId);
        return answer;
    }

    public static Answer project(UUID questionId, String projectUrl) {
        Answer answer = new Answer(questionId);
        answer.setProjectUrl(projectUrl);
        return answer;
    }

    public static Answer openText(UUID questionId, String textResponse) {
        Answer answer = new Answer(questionId);
        answer.setTextResponse(textResponse);
        return answer;
    }

    public boolean hasSelectedAlternative() {
        return selectedAlternativeId != null;
    }

    public boolean hasManualPayload() {
        return hasText(projectUrl) || hasText(textResponse);
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

    public String getTextResponse() {
        return textResponse;
    }

    public void setSelectedAlternativeId(UUID selectedAlternativeId) {
        this.selectedAlternativeId = selectedAlternativeId;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public void setTextResponse(String textResponse) {
        this.textResponse = textResponse;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
