package com.lia.liaprove.core.domain.question;

/**
 * Enumeração que define as áreas de conhecimento às quais uma questão pode pertencer.
 */
public enum KnowledgeArea {
    SOFTWARE_DEVELOPMENT("Software Development"),
    DATABASE("Database"),
    CYBERSECURITY("Cybersecurity"),
    NETWORKS("Networks"),
    AI("Artificial Intelligence");

    private final String displayName;

    KnowledgeArea(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}