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
    private final Map<UUID, FeedbackReaction> reactionsByUser = new LinkedHashMap<>();

    public FeedbackQuestion() {}

    public FeedbackQuestion(UUID id, User user, String comment, LocalDateTime submissionDate,
                            Question question, DifficultyLevel difficultyLevel, KnowledgeArea knowledgeArea,
                            RelevanceLevel relevanceLevel) {
        super(id, user, comment, submissionDate, true);
        this.question = question;
        this.difficultyLevel = difficultyLevel;
        this.knowledgeArea = knowledgeArea;
        this.relevanceLevel = relevanceLevel;
    }

    public Question getQuestion() {
        return question;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        touchUpdatedAt();
    }

    public KnowledgeArea getKnowledgeArea() {
        return knowledgeArea;
    }

    public void setKnowledgeArea(KnowledgeArea knowledgeArea) {
        this.knowledgeArea = knowledgeArea;
        touchUpdatedAt();
    }

    public RelevanceLevel getRelevanceLevel() {
        return relevanceLevel;
    }

    public void setRelevanceLevel(RelevanceLevel relevanceLevel) {
        this.relevanceLevel = relevanceLevel;
        touchUpdatedAt();
    }

    /** Retorna as reações na ordem de inserção, como lista imutável. */
    public List<FeedbackReaction> getReactions() {
        return List.copyOf(reactionsByUser.values());
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
