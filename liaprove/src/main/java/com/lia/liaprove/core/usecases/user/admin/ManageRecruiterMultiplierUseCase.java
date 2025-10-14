package com.lia.liaprove.core.usecases.user.admin;

import com.lia.liaprove.core.domain.user.UserRole;

import java.util.UUID;

/**
 * Use case para gerenciar multiplicadores de peso por role ou por recruiter individual.
 *
 * <p>Permite ajustar dinamicamente a influência relativa dos votos (ex.: {@code roleMultiplier})
 * tanto ao nível global por {@link UserRole} quanto para um {@code recruiterId} específico.
 */
public interface ManageRecruiterMultiplierUseCase {
    /**
     * Define o multiplicador associado a um {@link UserRole} (por exemplo: PROFESSIONAL=1.0, RECRUITER=3.0).
     *
     * @param role       role cujo multiplicador será alterado
     * @param multiplier novo multiplicador (por exemplo 1.0, 3.0)
     * @param adminId    id do administrador/sistema que realizou a alteração
     */
    void setRoleMultiplier(UserRole role, double multiplier, UUID adminId);

    /**
     * Recupera o multiplicador atual associado a um {@link UserRole}.
     *
     * @param  role role a consultar
     * @return valor do multiplicador (double). Implementações podem retornar um valor padrão
     */
    double getRoleMultiplier(UserRole role);

    /**
     * Define um multiplicador específico para um recruiter (override do multiplicador por role).
     *
     * @param recruiterId id do recruiter alvo
     * @param multiplier  novo multiplicador para este recruiter
     * @param adminId     id do administrador/sistema que realizou a alteração
     */
    void setRecruiterMultiplier(UUID recruiterId, double multiplier, UUID adminId);

    /**
     * Recupera o multiplicador específico de um recruiter, se houver.
     *
     * @param  recruiterId id do recruiter
     * @return multiplicador do recruiter (Double) ou {@code null} se não houver override (usar multiplicador por role)
     */
    Double getRecruiterMultiplier(UUID recruiterId);
}

