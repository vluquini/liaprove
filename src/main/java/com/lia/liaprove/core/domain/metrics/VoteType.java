package com.lia.liaprove.core.domain.metrics;

/**
 * Enum que define os tipos de voto possíveis em uma questão.
 * Utilizado pela entidade {@link Vote}.
 * <ul>
 *   <li>{@code APPROVE}: Indica um voto para aprovar a questão.</li>
 *   <li>{@code REJECT}: Indica um voto para rejeitar a questão.</li>
 * </ul>
 */
public enum VoteType {
    APPROVE, REJECT
}
