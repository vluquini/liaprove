package com.lia.liaprove.infrastructure.dtos.question;

import lombok.Data;
import java.util.UUID;

@Data
public class AlternativeDto {
    private UUID id;
    private String text;
}
