package com.lia.liaprove.core.domain.question;

public enum RelevanceLevel {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int relevanceLevel;

    RelevanceLevel(int relevanceLevel) {
        this.relevanceLevel = relevanceLevel;
    }

    public int getRelevanceLevel() {
        return relevanceLevel;
    }

    // Converte de int para enum, lançando exceção em caso inválido
    public static RelevanceLevel of(int relevanceLevel) {
        for (RelevanceLevel opt : values()) {
            if (opt.relevanceLevel == relevanceLevel) {
                return opt;
            }
        }
        throw new IllegalArgumentException("Valor inválido: " + relevanceLevel);
    }

}
