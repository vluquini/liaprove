package com.lia.liaprove.infrastructure.dtos.question;

import lombok.Data;
import java.util.UUID;

@Data
public class AlternativeResponseDto {
    private UUID id;
    private String text;
}
