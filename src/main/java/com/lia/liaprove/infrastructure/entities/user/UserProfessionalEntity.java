package com.lia.liaprove.infrastructure.entities.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("PROFESSIONAL")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserProfessionalEntity extends UserEntity {
    @ElementCollection
    @CollectionTable(name = "user_professional_hard_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill", nullable = false)
    @OrderColumn(name = "skill_order")
    private List<String> hardSkills = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_professional_soft_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill", nullable = false)
    @OrderColumn(name = "skill_order")
    private List<String> softSkills = new ArrayList<>();
}
