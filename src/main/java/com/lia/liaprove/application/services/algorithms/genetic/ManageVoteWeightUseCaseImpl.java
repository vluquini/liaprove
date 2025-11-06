package com.lia.liaprove.application.services.algorithms.genetic;

import com.lia.liaprove.application.gateways.algorithms.genetic.VoteMultiplierGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.usecases.algorithms.genetic.GeneticAlgorithmUseCase;
import com.lia.liaprove.core.usecases.user.admin.ManageVoteWeightUseCase;

import java.util.*;

/**
 * Orquestrador que:
 *  - executa o GA (via GeneticAlgorithmUseCase),
 *  - carrega os recruiters via UserGateway,
 *  - aplica o novo voteWeight via método de domínio (setVoteWeightSafely(min,max)),
 *  - persiste em lote via UserGateway.saveAll(...).
 *  - Gerencia pesos e multiplicadores de voto.
 */
public class ManageVoteWeightUseCaseImpl implements ManageVoteWeightUseCase {

    private final GeneticAlgorithmUseCase geneticUseCase;
    private final UserGateway userGateway;
    private final VoteMultiplierGateway voteMultiplierGateway;
    private final GeneticConfig config;

    public ManageVoteWeightUseCaseImpl(GeneticAlgorithmUseCase geneticUseCase, UserGateway userGateway, VoteMultiplierGateway voteMultiplierGateway, GeneticConfig config) {
        this.geneticUseCase = Objects.requireNonNull(geneticUseCase);
        this.userGateway = Objects.requireNonNull(userGateway);
        this.voteMultiplierGateway = Objects.requireNonNull(voteMultiplierGateway);
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

        applyAndPersistWeights(newWeights);
        return Map.copyOf(newWeights);
    }

    @Override
    public void applyManualWeights(Map<UUID, Integer> weights, UUID adminId) {
        if (weights == null || weights.isEmpty()) return;
        applyAndPersistWeights(weights);
    }

    @Override
    public void setRecruiterVoteWeight(UUID recruiterId, int newWeight, UUID adminId) {
        applyAndPersistWeights(Map.of(recruiterId, newWeight));
    }

    @Override
    public void setRoleMultiplier(UserRole role, double multiplier, UUID adminId) {
        voteMultiplierGateway.setRoleMultiplier(role, multiplier);
    }

    @Override
    public double getRoleMultiplier(UserRole role) {
        // Retorna o multiplicador específico da role ou 1.0 como um padrão seguro.
        return voteMultiplierGateway.getRoleMultiplier(role).orElse(1.0);
    }

    @Override
    public void setRecruiterMultiplier(UUID recruiterId, double multiplier, UUID adminId) {
        voteMultiplierGateway.setRecruiterMultiplier(recruiterId, multiplier);
    }

    @Override
    public Double getRecruiterMultiplier(UUID recruiterId) {
        // Retorna o multiplicador de override do recrutador ou null se não houver.
        return voteMultiplierGateway.getRecruiterMultiplier(recruiterId).orElse(null);
    }

    private List<UserRecruiter> applyAndPersistWeights(Map<UUID, Integer> weights) {
        if (weights == null || weights.isEmpty()) return List.of();

        Map<UUID, User> usersMap = userGateway.findByIdsAsMap(weights.keySet());
        if (usersMap == null || usersMap.isEmpty()) return List.of();

        List<UserRecruiter> toSave = applyWeightsAndCollect(weights, usersMap);
        if (toSave.isEmpty()) return List.of();

        List<User> saved = userGateway.saveAll(new ArrayList<>(toSave));

        return saved.stream()
                .filter(u -> u instanceof UserRecruiter)
                .map(u -> (UserRecruiter) u)
                .toList();
    }

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