package com.lia.liaprove.infrastructure.entities.question;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;


@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
@EqualsAndHashCode(callSuper = true)
@Data
public class MultipleChoiceQuestionEntity extends QuestionEntity {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "ord_index") // JPA armazena index
    @CollectionTable(name = "question_alternatives", joinColumns = @JoinColumn(name = "question_id"))
    private List<AlternativeEntity> alternatives = new ArrayList<>();

    // Helper methods to keep both sides of the relationship in sync
    public void addAlternative(AlternativeEntity alternative) {
        if (alternative == null) return;

        if (!alternatives.contains(alternative))
            alternatives.add(alternative);

        alternative.setQuestion(this);
    }

    public void removeAlternative(AlternativeEntity alternative) {
        if (alternative == null) return;

        if (alternatives.remove(alternative))
            alternative.setQuestion(null);
    }
}
