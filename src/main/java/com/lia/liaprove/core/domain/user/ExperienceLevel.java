package com.lia.liaprove.core.domain.user;

import java.util.Locale;
import java.util.Objects;

public enum ExperienceLevel {
    JUNIOR(1, "junior"),
    PLENO(2, "pleno"),
    SENIOR(3, "senior");

    private final int level;
    private final String label;

    ExperienceLevel(int level, String label) {
        this.level = level;
        this.label = label;
    }

    /** inteiro relativo ao nível (útil para ordenações / cálculos) */
    public int getLevel() {
        return level;
    }

    /** rótulo textual (em minúsculas) */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    /**
     * Parse seguro: aceita "junior", "JUNIOR", "Junior" e também nomes do enum.
     * Lança IllegalArgumentException se inválido.
     */
    public static ExperienceLevel fromString(String s) {
        if (s == null) throw new IllegalArgumentException("value must not be null");
        String normalized = s.trim().toLowerCase(Locale.ROOT);
        for (ExperienceLevel el : values()) {
            if (el.label.equals(normalized) || el.name().equalsIgnoreCase(normalized)) {
                return el;
            }
        }
        throw new IllegalArgumentException("Unknown ExperienceLevel: " + s);
    }

    /** tenta parse, retorna fallback se inválido (útil em processamento tolerante) */
    public static ExperienceLevel fromStringOrDefault(String s, ExperienceLevel fallback) {
        try {
            return fromString(s);
        } catch (IllegalArgumentException e) {
            return Objects.requireNonNull(fallback);
        }
    }
}
