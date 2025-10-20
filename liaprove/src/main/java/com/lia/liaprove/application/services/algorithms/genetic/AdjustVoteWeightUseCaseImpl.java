package com.lia.liaprove.application.services.algorithms.genetic;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.usecases.algorithms.genetic.GeneticAlgorithmUseCase;
import com.lia.liaprove.core.usecases.user.admin.AdjustVoteWeightUseCase;

import java.util.*;

/**
 * Orquestrador que:
 *  - executa o GA (via GeneticAlgorithmUseCase),
 *  - carrega os recruiters via UserGateway,
 *  - aplica o novo voteWeight via método de domínio (setVoteWeightSafely(min,max)),
 *  - persiste em lote via UserGateway.saveAll(...).
 */
public class AdjustVoteWeightUseCaseImpl implements AdjustVoteWeightUseCase {

    private final GeneticAlgorithmUseCase geneticUseCase;
    private final UserGateway userGateway;
    private final GeneticConfig config;

    public AdjustVoteWeightUseCaseImpl(GeneticAlgorithmUseCase geneticUseCase, UserGateway userGateway, GeneticConfig config) {
        this.geneticUseCase = Objects.requireNonNull(geneticUseCase);
        this.userGateway = Objects.requireNonNull(userGateway);
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public Map<UUID, Integer> adjustAllRecruiterWeights(boolean dryRun, UUID triggeredByAdminId) {
        Map<UUID, Integer> newWeights = geneticUseCase.runAdjustVoteWeights();

        if (newWeights == null || newWeights.isEmpty()) {
            return Map.of();
        }

        if (dryRun) {
            return Map.copyOf(newWeights);
        }

        // aplica e persiste; captura recrutadores persistidos (pode ser usado para logs)
        List<UserRecruiter> saved = applyAndPersistWeights(newWeights);

        // opcional: log info sobre quantos foram atualizados
        // logger.info("Adjusted vote weights for {} recruiters (triggeredBy={})", saved.size(), triggeredByAdminId);

        return Map.copyOf(newWeights);
    }

    @Override
    public void applyManualWeights(Map<UUID, Integer> weights, UUID adminId) {
        if (weights == null || weights.isEmpty()) return;

        List<UserRecruiter> saved = applyAndPersistWeights(weights);
    }

    /**
     * Carrega usuários em batch, aplica os weights via domínio e persiste em lote.
     * Retorna a lista de UserRecruiter persistidos (padrão: imutável).
     */
    private List<UserRecruiter> applyAndPersistWeights(Map<UUID, Integer> weights) {
        if (weights == null || weights.isEmpty()) return List.of();

        // 1) load users as map
        Map<UUID, User> usersMap = userGateway.findByIdsAsMap(weights.keySet());
        if (usersMap == null || usersMap.isEmpty()) return List.of();

        // 2) apply weights (reutiliza método que já existe)
        List<UserRecruiter> toSave = applyWeightsAndCollect(weights, usersMap);
        if (toSave.isEmpty()) return List.of();

        // 3) persist and capture returned entities
        List<User> saved = userGateway.saveAll(new ArrayList<>(toSave));

        // 4) convert returned Users to UserRecruiter where applicable
        List<UserRecruiter> savedRecruiters = saved.stream()
                .filter(u -> u instanceof UserRecruiter)
                .map(u -> (UserRecruiter) u)
                .toList(); // Java 21 -> returns unmodifiable list

        return savedRecruiters;
    }

    @Override
    public void rollbackLastAdjustment(UUID adminId) {
        throw new UnsupportedOperationException("Rollback não implementado; requer historização (audit).");
    }

    /**
     * Aplica os weights (map id->targetWeight) sobre os usuários fornecidos no usersMap.
     * Retorna a lista de UserRecruiter alterados (prontos para persistência).
     */
    private List<UserRecruiter> applyWeightsAndCollect(Map<UUID, Integer> weights, Map<UUID, User> usersMap) {
        if (weights == null || weights.isEmpty() || usersMap == null || usersMap.isEmpty()) {
            return Collections.emptyList();
        }

        int min = config.getMinWeight();
        int max = config.getMaxWeight();

        List<UserRecruiter> result = new ArrayList<>(weights.size());

        for (Map.Entry<UUID, Integer> e : weights.entrySet()) {
            UUID id = e.getKey();
            Integer targetWeight = e.getValue();
            if (targetWeight == null) continue;

            User u = usersMap.get(id);
            if (!(u instanceof UserRecruiter)) {
                // não é recruiter (ou não encontrado) — ignorar
                continue;
            }

            UserRecruiter recruiter = (UserRecruiter) u;
            int bounded = Math.max(min, Math.min(max, targetWeight));
            recruiter.setVoteWeightSafely(bounded, min, max);
            result.add(recruiter);
        }

        return result;
    }
}

