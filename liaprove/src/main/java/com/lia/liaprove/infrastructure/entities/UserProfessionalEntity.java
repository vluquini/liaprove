package com.lia.liaprove.infrastructure.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("PROFESSIONAL")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserProfessionalEntity extends UserEntity {
}
