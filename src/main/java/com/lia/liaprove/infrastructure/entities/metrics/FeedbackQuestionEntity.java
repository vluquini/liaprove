package com.lia.liaprove.infrastructure.entities.metrics;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("QUESTION")
@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackQuestionEntity extends FeedbackEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "knowledge_area")
    private KnowledgeArea knowledgeArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "relevance_level")
    private RelevanceLevel relevanceLevel;

    @OneToMany(mappedBy = "feedbackQuestion", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "ord_index")
    private List<FeedbackReactionEntity> reactions = new ArrayList<>();

    public void addReaction(FeedbackReactionEntity reaction) {
        this.reactions.add(reaction);
        reaction.setFeedbackQuestion(this);
    }

    public void removeReaction(FeedbackReactionEntity reaction) {
        this.reactions.remove(reaction);
        reaction.setFeedbackQuestion(null);
    }
}
