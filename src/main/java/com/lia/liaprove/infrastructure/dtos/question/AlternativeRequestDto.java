package com.lia.liaprove.infrastructure.dtos.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlternativeRequestDto {
    @NotBlank
    @Size(max = 255)
    private String text;
    private boolean correct;
}
