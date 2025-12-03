package com.lia.liaprove.infrastructure.entities.question;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
@Getter
@Setter
public class MultipleChoiceQuestionEntity extends QuestionEntity {
    @ElementCollection(fetch = FetchType.LAZY) // LAZY to avoid over-fetching
    @CollectionTable(name = "question_alternatives", joinColumns = @JoinColumn(name = "question_id"))
    private List<AlternativeEmbeddable> alternatives = new ArrayList<>();
}
