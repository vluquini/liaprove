package com.lia.liaprove.infrastructure.dtos.user;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Size(min = 2, max = 100)
    private String name;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 100)
    private String occupation;

    @Size(max = 1000)
    private String bio;

    private ExperienceLevel experienceLevel;
}
