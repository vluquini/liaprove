package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.*;

/*
Entidade usada para registrar os votos dos usuários
nas questões em fase de votação.
*/
public class FeedbackQuestion extends Feedback{
    private Question question;
    // A comunidade avaliará o nível de dificuldade da questão
    private DifficultyLevel difficultyLevel;
    // A comunidade avaliará a área de conhecimento da questão
    private KnowledgeArea knowledgeArea;
    private RelevanceLevel relevanceLevel;
    // Reações (likes/dislikes) — usamos um Map de id->user para evitar duplicidade; LinkedHashMap para preservar ordem de inserção
    private final Map<UUID, FeedbackReaction> reactionsByUser = new LinkedHashMap<>();

    public FeedbackQuestion() {}

    public FeedbackQuestion(UUID id, User user, String comment, Vote vote, LocalDateTime submissionDate, Question question, DifficultyLevel difficultyLevel, KnowledgeArea knowledgeArea, RelevanceLevel relevanceLevel) {
        super(id, user, comment, vote, submissionDate);
        this.question = question;
        this.difficultyLevel = difficultyLevel;
        this.knowledgeArea = knowledgeArea;
        this.relevanceLevel = relevanceLevel;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public KnowledgeArea getKnowledgeArea() {
        return knowledgeArea;
    }

    public void setKnowledgeArea(KnowledgeArea knowledgeArea) {
        this.knowledgeArea = knowledgeArea;
    }

    public RelevanceLevel getRelevanceLevel() {
        return relevanceLevel;
    }

    public void setRelevanceLevel(RelevanceLevel relevanceLevel) {
        this.relevanceLevel = relevanceLevel;
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
            return true;
        } else {
            FeedbackReaction reaction = new FeedbackReaction(user, type);
            reactionsByUser.put(userId, reaction);
            return true;
        }
    }

    /** Remove reação do usuário; retorna true se removida. */
    public boolean removeReaction(User user) {
        Objects.requireNonNull(user, "user");
        return reactionsByUser.remove(user.getId()) != null;
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
