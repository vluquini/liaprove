package com.lia.liaprove.infrastructure.entities.question;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("PROJECT")
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectQuestionEntity extends QuestionEntity {
    @Column(name = "project_url", length = 500, nullable = true)
    private String projectUrl;
}
