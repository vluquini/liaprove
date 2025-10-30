package com.lia.liaprove.core.domain.question;

/**
 * Enumeração que controla o status de aprovação de uma questão na plataforma.
 * <ul>
 *     <li>{@code VOTING}: A questão está em processo de votação pela comunidade.</li>
 *     <li>{@code APPROVED}: A questão foi aprovada pela comunidade, mas ainda não será usada nas avaliações.</li>
 *     <li>{@code FINISHED}: A questão foi aprovada e já pode ser utilizada em avaliações.</li>
 *     <li>{@code REJECTED}: A questão foi rejeitada na votação pela comunidade.</li>
 * </ul>
 */
public enum QuestionStatus {
    VOTING, APPROVED, FINISHED, REJECTED
}
