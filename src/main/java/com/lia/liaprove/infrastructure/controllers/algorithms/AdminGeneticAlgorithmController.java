package com.lia.liaprove.infrastructure.controllers.algorithms;

import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.usecases.algorithms.genetic.ManageVoteWeightUseCase;
import com.lia.liaprove.infrastructure.dtos.algorithms.genetic.AdjustGeneticWeightsRequest;
import com.lia.liaprove.infrastructure.dtos.algorithms.genetic.UpdateMultiplierRequest;
import com.lia.liaprove.infrastructure.dtos.algorithms.genetic.UpdateVoteWeightRequest;
import com.lia.liaprove.infrastructure.security.SecurityContextService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/algorithms/genetic")
@PreAuthorize("hasRole('ADMIN')")
public class AdminGeneticAlgorithmController {

    private final ManageVoteWeightUseCase manageVoteWeightUseCase;
    private final SecurityContextService securityContextService;

    public AdminGeneticAlgorithmController(ManageVoteWeightUseCase manageVoteWeightUseCase,
                                           SecurityContextService securityContextService) {
        this.manageVoteWeightUseCase = manageVoteWeightUseCase;
        this.securityContextService = securityContextService;
    }

    @PostMapping("/adjust")
    public ResponseEntity<Map<UUID, Integer>> adjustAllRecruiterWeights(@Valid @RequestBody AdjustGeneticWeightsRequest request) {
        UUID adminId = securityContextService.getCurrentUserId();
        boolean dryRun = Boolean.TRUE.equals(request.dryRun());
        Map<UUID, Integer> result = manageVoteWeightUseCase.adjustAllRecruiterWeights(dryRun, adminId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/recruiters/{id}/vote-weight")
    public ResponseEntity<Void> setRecruiterVoteWeight(@PathVariable UUID id,
                                                       @Valid @RequestBody UpdateVoteWeightRequest request) {
        UUID adminId = securityContextService.getCurrentUserId();
        manageVoteWeightUseCase.setRecruiterVoteWeight(id, request.weight(), adminId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/roles/{role}/multiplier")
    public ResponseEntity<Void> setRoleMultiplier(@PathVariable UserRole role,
                                                  @Valid @RequestBody UpdateMultiplierRequest request) {
        UUID adminId = securityContextService.getCurrentUserId();
        manageVoteWeightUseCase.setRoleMultiplier(role, request.multiplier(), adminId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles/{role}/multiplier")
    public ResponseEntity<Double> getRoleMultiplier(@PathVariable UserRole role) {
        Double multiplier = manageVoteWeightUseCase.getRoleMultiplier(role);
        return ResponseEntity.ok(multiplier);
    }

    @PatchMapping("/recruiters/{id}/multiplier")
    public ResponseEntity<Void> setRecruiterMultiplier(@PathVariable UUID id,
                                                       @Valid @RequestBody UpdateMultiplierRequest request) {
        UUID adminId = securityContextService.getCurrentUserId();
        manageVoteWeightUseCase.setRecruiterMultiplier(id, request.multiplier(), adminId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recruiters/{id}/multiplier")
    public ResponseEntity<Double> getRecruiterMultiplier(@PathVariable UUID id) {
        Double multiplier = manageVoteWeightUseCase.getRecruiterMultiplier(id);
        return ResponseEntity.ok(multiplier);
    }
}
