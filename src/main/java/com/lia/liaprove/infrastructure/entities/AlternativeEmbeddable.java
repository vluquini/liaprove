package com.lia.liaprove.infrastructure.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AlternativeEmbeddable {
    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private boolean correct;
}
