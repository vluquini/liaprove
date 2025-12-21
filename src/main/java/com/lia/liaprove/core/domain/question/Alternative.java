package com.lia.liaprove.core.domain.question;

import java.util.UUID;

/**
 * Record que representa uma alternativa de resposta para questões de múltipla escolha,
 * indicando o texto e se é a resposta correta.
 */
public record Alternative(UUID id, String text, boolean correct) {
}
