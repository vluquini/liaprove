package com.lia.liaprove.infrastructure.entities.question;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "question_alternatives")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlternativeEntity {
    // Mesmas anotações de ID e tipo de ID para consistência entre entidades
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private boolean correct;

//    @Column(name = "ord_index") // Vale a pena este atributo?
//    private Integer orderIndex;
}
