package com.lia.liaprove.application.gateways.algorithms.genetic;

import com.lia.liaprove.core.domain.user.UserRole;

import java.util.Optional;
import java.util.UUID;

/**
 * Gateway/port para gerenciar os multiplicadores de voto.
 * A camada de infra será responsável por persistir e recuperar esses valores.
 */
public interface VoteMultiplierGateway {

    /**
     * Define o multiplicador associado a um UserRole.
     *
     * @param role A role cujo multiplicador será alterado.
     * @param multiplier O novo valor do multiplicador.
     */
    void setRoleMultiplier(UserRole role, double multiplier);

    /**
     * Recupera o multiplicador atual associado a um UserRole.
     *
     * @param role A role a consultar.
     * @return O valor do multiplicador, ou Optional.empty() se não configurado.
     */
    Optional<Double> getRoleMultiplier(UserRole role);

    /**
     * Define um multiplicador específico para um recrutador individual.
     *
     * @param recruiterId O ID do recrutador alvo.
     * @param multiplier O novo multiplicador para este recrutador.
     */
    void setRecruiterMultiplier(UUID recruiterId, double multiplier);

    /**
     * Recupera o multiplicador específico de um recrutador, se houver um override.
     *
     * @param recruiterId O ID do recrutador.
     * @return O multiplicador do recrutador, ou Optional.empty() se não houver override.
     */
    Optional<Double> getRecruiterMultiplier(UUID recruiterId);
}