package com.lia.liaprove.core.usecases.user.admin;

import com.lia.liaprove.core.domain.user.UserRole;

import java.util.Map;
import java.util.UUID;

/**
 * Use case administrativo para gerenciar pesos de votos e multiplicadores.
 * Consolida funcionalidades de ajuste de pesos de votos por algoritmos,
 * definição manual de pesos para usuários e gerenciamento de multiplicadores
 * por role ou por recruiter individual.
 */
public interface ManageVoteWeightUseCase {

    /**
     * Dispara o processo de ajuste de todos os pesos de voto dos recrutadores,
     * geralmente por um algoritmo (e.g., Genético).
     *
     * @param dryRun Indica se a execução deve ser apenas uma simulação, sem persistir os resultados.
     * @param triggeredByAdminId ID do administrador que disparou a ação.
     * @return Um mapa contendo o ID do recrutador e seu novo peso de voto ajustado.
     */
    Map<UUID, Integer> adjustAllRecruiterWeights(boolean dryRun, UUID triggeredByAdminId);

    /**
     * Aplica manualmente um mapa de pesos de voto para recrutadores específicos.
     * Isso pode sobrescrever os pesos calculados por algoritmos.
     *
     * @param weights Um mapa onde a chave é o ID do recrutador e o valor é o peso de voto.
     * @param adminId ID do administrador que realizou a alteração.
     */
    void applyManualWeights(Map<UUID, Integer> weights, UUID adminId);

    /**
     * Define um peso de voto específico para um recrutador.
     *
     * @param recruiterId O ID do recrutador alvo.
     * @param newWeight O novo peso de voto a ser atribuído.
     * @param adminId ID do administrador que realizou a alteração.
     */
    void setRecruiterVoteWeight(UUID recruiterId, int newWeight, UUID adminId);

    /**
     * Define o multiplicador associado a um {@link UserRole}.
     * Este multiplicador afeta o peso dos votos de todos os usuários com a role especificada.
     *
     * @param role A role cujo multiplicador será alterado.
     * @param multiplier O novo valor do multiplicador (e.g., 1.0, 3.0).
     * @param adminId ID do administrador que realizou a alteração.
     */
    void setRoleMultiplier(UserRole role, double multiplier, UUID adminId);

    /**
     * Recupera o multiplicador atual associado a um {@link UserRole}.
     *
     * @param role A role a consultar.
     * @return O valor do multiplicador. Implementações podem retornar um valor padrão se não configurado.
     */
    double getRoleMultiplier(UserRole role);

    /**
     * Define um multiplicador específico para um recrutador individual.
     * Este multiplicador sobrescreve o multiplicador por role para o recrutador especificado.
     *
     * @param recruiterId O ID do recrutador alvo.
     * @param multiplier O novo multiplicador para este recrutador.
     * @param adminId ID do administrador que realizou a alteração.
     */
    void setRecruiterMultiplier(UUID recruiterId, double multiplier, UUID adminId);

    /**
     * Recupera o multiplicador específico de um recrutador, se houver um override.
     *
     * @param recruiterId O ID do recrutador.
     * @return O multiplicador do recrutador (Double) ou {@code null} se não houver override (deve-se usar o multiplicador por role).
     */
    Double getRecruiterMultiplier(UUID recruiterId);
}
