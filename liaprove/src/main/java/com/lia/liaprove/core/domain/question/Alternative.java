package com.lia.liaprove.core.domain.question;

/**
 * Record que representa uma alternativa de resposta para questões de múltipla escolha,
 * indicando o texto e se é a resposta correta.
 */
public record Alternative(String text, boolean isCorrect) {
}
