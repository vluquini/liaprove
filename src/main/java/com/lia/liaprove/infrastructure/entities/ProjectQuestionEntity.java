package com.lia.liaprove.infrastructure.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("PROJECT")
@Getter
@Setter
public class ProjectQuestionEntity extends QuestionEntity {
    @Column(name = "project_url", length = 500, nullable = false)
    private String projectUrl;
}
