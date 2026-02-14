package com.lia.liaprove.infrastructure.dtos.user;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.lia.liaprove.infrastructure.validation.RecruiterFieldsRequired;
import lombok.Data;

@Data
@RecruiterFieldsRequired
public class CreateUserRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    private String occupation;

    private String bio;

    private ExperienceLevel experienceLevel;

    @NotNull
    private UserRole role;

    private String companyName;

    @Email(message = "Company email must be a valid email address.")
    private String companyEmail;
}
