package com.lia.liaprove.application.gateways.metrics;

import com.lia.liaprove.core.domain.metrics.Vote;

public interface VoteGateway {
    /**
     * Salva um voto no sistema.
     * @param vote O objeto Vote a ser salvo.
     */
    void save(Vote vote);
}
