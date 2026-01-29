package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entidade que representa um feedback (comentário) detalhado sobre uma questão,
 * incluindo sugestões de dificuldade, área de conhecimento e relevância.
 * Também gerencia as reações (likes/dislikes) a este feedback.
 */
public class FeedbackQuestion extends Feedback{
    private Question question;
    private DifficultyLevel difficultyLevel;
    private KnowledgeArea knowledgeArea;
    private RelevanceLevel relevanceLevel;
    // Reações (userId -> reaction). LinkedHashMap preserva ordem de inserção
    private Map<UUID, FeedbackReaction> reactionsByUser = new LinkedHashMap<>();

    public FeedbackQuestion() {}

    public FeedbackQuestion(User user, String comment, LocalDateTime submissionDate,
                            Question question, DifficultyLevel difficultyLevel, KnowledgeArea knowledgeArea,
                            RelevanceLevel relevanceLevel) {
        super(user, comment, submissionDate, true);
        this.question = question;
        this.difficultyLevel = difficultyLevel;
        this.knowledgeArea = knowledgeArea;
        this.relevanceLevel = relevanceLevel;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        if (this.question != null) {
            throw new IllegalStateException("The Question for this feedback has already been set and cannot be changed.");
        }
        this.question = question;
    }

    public void setReactionsByUser(Map<UUID, FeedbackReaction> reactionsByUser) {
        if (!this.reactionsByUser.isEmpty()) {
            throw new IllegalStateException("Feedback reactions have already been set and cannot be replaced.");
        }
        if (reactionsByUser != null) {
            // Cópia defensiva para proteger a integridade do mapa interno
            this.reactionsByUser = new LinkedHashMap<>(reactionsByUser);
        }
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        if (this.difficultyLevel != null) {
            throw new IllegalStateException("DifficultyLevel has already been set and cannot be changed.");
        }
        this.difficultyLevel = difficultyLevel;
        touchUpdatedAt();
    }

    public KnowledgeArea getKnowledgeArea() {
        return knowledgeArea;
    }

    public void setKnowledgeArea(KnowledgeArea knowledgeArea) {
        if (this.knowledgeArea != null) {
            throw new IllegalStateException("KnowledgeArea has already been set and cannot be changed.");
        }
        this.knowledgeArea = knowledgeArea;
        touchUpdatedAt();
    }

    public RelevanceLevel getRelevanceLevel() {
        return relevanceLevel;
    }

    public void setRelevanceLevel(RelevanceLevel relevanceLevel) {
        if (this.relevanceLevel != null) {
            throw new IllegalStateException("RelevanceLevel has already been set and cannot be changed.");
        }
        this.relevanceLevel = relevanceLevel;
        touchUpdatedAt();
    }

    /** Retorna as reações na ordem de inserção, como lista imutável. */
    public List<FeedbackReaction> getReactions() {
        return List.copyOf(reactionsByUser.values());
    }

    /**
     * Define as reações para este feedback a partir de uma lista.
     * Este método é primariamente para uso do frameworks de mapeamento (MapStruct)
     * e garante que as reações não sejam sobrescritas.
     *
     * @param reactions A lista de reações a ser adicionada.
     */
    public void setReactions(List<FeedbackReaction> reactions) {
        if (!this.reactionsByUser.isEmpty()) {
            throw new IllegalStateException("Feedback reactions have already been set and cannot be replaced.");
        }
        if (reactions != null) {
            for (FeedbackReaction reaction : reactions) {
                if (reaction != null && reaction.getUser() != null) {
                    this.reactionsByUser.put(reaction.getUser().getId(), reaction);
                }
            }
        }
    }

    /**
     * Adiciona ou atualiza a reação do usuário.
     * - Se user já reagiu, atualiza o tipo.
     * - Retorna true se criou/alterou; false se não houve alteração.
     */
    public boolean addOrUpdateReaction(User user, ReactionType type) {
        Objects.requireNonNull(user, "user");
        Objects.requireNonNull(type, "type");

        UUID userId = user.getId();
        FeedbackReaction existing = reactionsByUser.get(userId);
        if (existing != null) {
            if (existing.getType() == type) return false; // sem mudança
            existing.setType(type);
        } else {
            FeedbackReaction reaction = new FeedbackReaction(user, type);
            reactionsByUser.put(userId, reaction);
        }
        touchUpdatedAt();
        return true;
    }

    /** Remove reação do usuário; retorna true se removida. */
    public boolean removeReaction(User user) {
        Objects.requireNonNull(user, "user");
        UUID id = user.getId();
        FeedbackReaction removed = reactionsByUser.remove(id);
        if (removed != null){
            touchUpdatedAt();
            return true;
        }
        return false;
    }

    public long countLikes() {
        return reactionsByUser.values().stream().filter(r -> r.getType() == ReactionType.LIKE).count();
    }

    public long countDislikes() {
        return reactionsByUser.values().stream().filter(r -> r.getType() == ReactionType.DISLIKE).count();
    }

    public int netLikes() {
        return (int) (countLikes() - countDislikes());
    }

    public boolean userHasLiked(User user) {
        FeedbackReaction r = reactionsByUser.get(user.getId());
        return r != null && r.getType() == ReactionType.LIKE;
    }

    public ReactionType getReactionTypeByUser(User user) {
        FeedbackReaction r = reactionsByUser.get(user.getId());
        return r == null ? null : r.getType();
    }

    public double likeRatio() {
        long likes = countLikes();
        long dislikes = countDislikes();
        long total = likes + dislikes;
        if (total == 0) return 0.0;
        return (double) likes / (double) total;
    }
}
