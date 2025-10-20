package com.lia.liaprove.core.usecases.user.admin;

import java.util.Map;
import java.util.UUID;

/**
 * Caso de uso administrativo para ajuste de pesos de votos.
 * Implementação delega ao serviço de algoritmos.
 */
public interface AdjustVoteWeightUseCase {
    // dispara e persiste (scheduler/admin)
    Map<UUID,Integer> adjustAllRecruiterWeights(boolean dryRun, UUID triggeredByAdminId);

    // aplica manualmente um mapa de weights (override)
    void applyManualWeights(Map<UUID,Integer> weights, UUID adminId);

    // reverte última execução (opcional, em caso de armazenar audit/history)
    void rollbackLastAdjustment(UUID adminId);
}