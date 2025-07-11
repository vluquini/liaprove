package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.UUID;
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

    public FeedbackQuestion(UUID id, User user, String comment, Vote vote, LocalDateTime submissionDate, Question question, DifficultyLevel difficultyLevel, KnowledgeArea knowledgeArea, RelevanceLevel relevanceLevel) {
        super(id, user, comment, vote, submissionDate);
        this.question = question;
        this.difficultyLevel = difficultyLevel;
        this.knowledgeArea = knowledgeArea;
        this.relevanceLevel = relevanceLevel;
    }
}
