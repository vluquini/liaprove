package com.lia.liaprove.core.domain.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Representa uma questão de múltipla escolha, estendendo a classe Question e incluindo uma lista de alternativas.
 */
public class MultipleChoiceQuestion extends Question {
    private List<Alternative> alternatives;

    public MultipleChoiceQuestion(UUID id, UUID authorId, String title, String description, Set<KnowledgeArea> knowledgeAreas,
                                  DifficultyLevel difficultyByCommunity, RelevanceLevel relevanceByCommunity,
                                  LocalDateTime submissionDate, QuestionStatus status, RelevanceLevel relevanceByLLM,
                                  int recruiterUsageCount, List<Alternative> alternatives) {
        super(id, authorId, title, description, knowledgeAreas, difficultyByCommunity, relevanceByCommunity,
              submissionDate, status, relevanceByLLM, recruiterUsageCount);
        this.alternatives = alternatives;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }
}
