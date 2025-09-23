package com.lia.liaprove.application.gateways.algorithms.genetic;

import com.lia.liaprove.core.domain.user.UserRecruiter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta para operações de leitura e escrita relacionadas a Recruiters
 * que o algoritmo genético precisa.
 */
public interface RecruiterGateway {

    /**
     * Retorna todos os recruiters relevantes para o ajuste de pesos.
     * A implementação pode filtrar por status/ativo/preferências.
     */
    List<UserRecruiter> findAllRecruiters();

    /**
     * Busca um recruiter por id.
     */
    Optional<UserRecruiter> findById(UUID id);

    /**
     * Persiste/atualiza um recruiter (por ex., após ajustar voteWeight).
     * Implementação poderá usar JPA/repository.
     */
    void save(UserRecruiter recruiter);

    /**
     * Atualiza diretamente apenas o weight do recruiter (conveniente e eficiente).
     * Implementações podem executar um update parcial no DB.
     */
    void updateVoteWeight(UUID recruiterId, int newWeight);
}
