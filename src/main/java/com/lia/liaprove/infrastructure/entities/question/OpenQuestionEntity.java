package com.lia.liaprove.infrastructure.entities.question;

import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("OPEN")
@EqualsAndHashCode(callSuper = true)
@Data
public class OpenQuestionEntity extends QuestionEntity {

    @Column(name = "guideline", length = 2000)
    private String guideline;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private OpenQuestionVisibility visibility;
}
