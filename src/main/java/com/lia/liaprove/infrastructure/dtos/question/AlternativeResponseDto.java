package com.lia.liaprove.infrastructure.dtos.question;

import java.util.UUID;

public record AlternativeResponseDto(
        UUID id,
        String text
) {}
