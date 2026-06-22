package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;

/**
 * Entidade que representa um feedback (comentário) detalhado sobre uma questão,
 * incluindo sugestões de dificuldade, área de conhecimento e relevância.
 */
public class FeedbackQuestion extends Feedback{
    private Question question;
    private DifficultyLevel difficultyLevel;
    private KnowledgeArea knowledgeArea;
    private RelevanceLevel relevanceLevel;

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
        if (this.question != null && !this.question.equals(question)) {
            throw new IllegalStateException("The Question for this feedback has already been set and cannot be changed.");
        }
        this.question = question;
        touchUpdatedAt();
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        if (this.difficultyLevel != null && !this.difficultyLevel.equals(difficultyLevel)) {
            throw new IllegalStateException("DifficultyLevel has already been set and cannot be changed.");
        }
        this.difficultyLevel = difficultyLevel;
        touchUpdatedAt();
    }

    public KnowledgeArea getKnowledgeArea() {
        return knowledgeArea;
    }

    public void setKnowledgeArea(KnowledgeArea knowledgeArea) {
        if (this.knowledgeArea != null && !this.knowledgeArea.equals(knowledgeArea)) {
            throw new IllegalStateException("KnowledgeArea has already been set and cannot be changed.");
        }
        this.knowledgeArea = knowledgeArea;
        touchUpdatedAt();
    }

    public RelevanceLevel getRelevanceLevel() {
        return relevanceLevel;
    }

    public void setRelevanceLevel(RelevanceLevel relevanceLevel) {
        if (this.relevanceLevel != null && !this.relevanceLevel.equals(relevanceLevel)) {
            throw new IllegalStateException("RelevanceLevel has already been set and cannot be changed.");
        }
        this.relevanceLevel = relevanceLevel;
        touchUpdatedAt();
    }
}
