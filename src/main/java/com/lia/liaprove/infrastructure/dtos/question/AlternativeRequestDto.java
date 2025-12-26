package com.lia.liaprove.infrastructure.dtos.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlternativeRequestDto(
        @NotBlank
        @Size(max = 255)
        String text,
        boolean correct
) {}
