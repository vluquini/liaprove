package com.lia.liaprove.core.domain.question;

public class ProjectQuestion extends Question {
    // Link do projeto desenvolvido pelo Usuário
    private String projectUrl;

    public ProjectQuestion(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }
}
