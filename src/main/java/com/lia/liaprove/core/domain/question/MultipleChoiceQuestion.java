package com.lia.liaprove.core.domain.question;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Representa uma questão de múltipla escolha, estendendo a classe Question e incluindo uma lista de alternativas.
 */
public class MultipleChoiceQuestion extends Question {
    private final List<Alternative> alternatives;

    public MultipleChoiceQuestion(UUID id, UUID authorId, String title, String description, Set<KnowledgeArea> knowledgeAreas,
                                  DifficultyLevel difficultyByCommunity, RelevanceLevel relevanceByCommunity,
                                  LocalDateTime submissionDate, QuestionStatus status, RelevanceLevel relevanceByLLM,
                                  int recruiterUsageCount, List<Alternative> alternatives) {
        super(id, authorId, title, description, knowledgeAreas, difficultyByCommunity, relevanceByCommunity,
              submissionDate, status, relevanceByLLM, recruiterUsageCount);

        if (alternatives == null) {
            throw new IllegalArgumentException("Alternatives must not be null.");
        }
        if (alternatives.size() < 3 || alternatives.size() > 5) {
            throw new IllegalArgumentException("Multiple choice questions must have between 3 and 5 alternatives.");
        }

        Set<String> seen = new HashSet<>();
        int correctCount = 0;

        for (int i = 0; i < alternatives.size(); i++) {
            Alternative alt = alternatives.get(i);
            if (alt == null) {
                throw new IllegalArgumentException("Alternative at index " + i + " must not be null.");
            }
            String text = alt.text();
            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException("Alternative text must not be blank.");
            }
            if (alt.correct()) correctCount++;

            String normalized = normalizeText(text);
            if (!seen.add(normalized)) {
                throw new IllegalArgumentException("Alternatives must have unique texts (ignoring case, spaces and accents).");
            }
        }

        if (correctCount != 1) {
            throw new IllegalArgumentException("There must be exactly one correct alternative. Found " + correctCount);
        }

        this.alternatives = List.copyOf(alternatives);
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    /**
     * Normaliza um texto para comparação consistente:
     * <ul>
     *   <li>Remove espaços em branco no início e fim.</li>
     *   <li>Decompõe caracteres acentuados (NFD) e remove os acentos.</li>
     *   <li>Converte o texto para minúsculas usando {@link Locale#ROOT}
     *       para garantir comportamento estável e independente de idioma.</li>
     * </ul>
     *
     * @param s o texto de entrada
     * @return o texto normalizado, sem acentos, sem espaços extras e em minúsculas
     */
    private static String normalizeText(String s) {
        String trimmed = s.trim();
        String normalized = java.text.Normalizer.normalize(trimmed, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
