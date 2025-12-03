package com.lia.liaprove.infrastructure.entities.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RECRUITER")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRecruiterEntity extends UserEntity {
    private String companyName;
    private String companyEmail;
    private Integer totalAssessmentsCreated;
    private Float recruiterRating;
    private Integer recruiterRatingCount;
}
