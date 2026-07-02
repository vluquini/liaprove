package com.lia.liaprove.application.services.algorithms.genetic;

import com.lia.liaprove.application.gateways.algorithms.genetic.VoteMultiplierGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.usecases.algorithms.genetic.GeneticAlgorithmUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ManageVoteWeightUseCaseImplTest {

    private final UUID recruiterId = UUID.randomUUID();
    private final UUID professionalId = UUID.randomUUID();
    private GeneticAlgorithmUseCase geneticUseCase;
    private UserGateway userGateway;
    private VoteMultiplierGateway voteMultiplierGateway;
    private ManageVoteWeightUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        geneticUseCase = mock(GeneticAlgorithmUseCase.class);
        userGateway = mock(UserGateway.class);
        voteMultiplierGateway = mock(VoteMultiplierGateway.class);
        useCase = new ManageVoteWeightUseCaseImpl(
                geneticUseCase,
                userGateway,
                voteMultiplierGateway,
                GeneticConfig.defaults()
        );
    }

    @Test
    @DisplayName("Should return dry run weights without persisting")
    void shouldReturnDryRunWeightsWithoutPersisting() {
        when(geneticUseCase.runAdjustVoteWeights()).thenReturn(Map.of(recruiterId, 7));

        Map<UUID, Integer> result = useCase.adjustAllRecruiterWeights(true);

        assertThat(result).containsEntry(recruiterId, 7);
        verifyNoInteractions(userGateway);
    }

    @Test
    @DisplayName("Should apply manual weights only to recruiters")
    void shouldApplyManualWeightsOnlyToRecruiters() {
        UserRecruiter recruiter = recruiter(recruiterId, 3);
        UserProfessional professional = professional(professionalId, 3);
        when(userGateway.findByIdsAsMap(any())).thenReturn(Map.of(
                recruiterId, recruiter,
                professionalId, professional
        ));
        when(userGateway.saveAll(any())).thenReturn(List.of(recruiter));

        useCase.applyManualWeights(Map.of(recruiterId, 8, professionalId, 9));

        assertThat(recruiter.getVoteWeight()).isEqualTo(8);
        assertThat(professional.getVoteWeight()).isEqualTo(3);
        verify(userGateway).saveAll(argThat(this::containsOnlyRecruiters));
    }

    @Test
    @DisplayName("Should reject invalid multiplier")
    void shouldRejectInvalidMultiplier() {
        assertThatThrownBy(() -> useCase.setRoleMultiplier(UserRole.RECRUITER, Double.NaN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("multiplier must be finite and >= 0.0");
    }

    private boolean containsOnlyRecruiters(Collection<User> users) {
        return users.size() == 1 && users.iterator().next() instanceof UserRecruiter;
    }

    private UserRecruiter recruiter(UUID id, int voteWeight) {
        UserRecruiter recruiter = new UserRecruiter();
        recruiter.setId(id);
        recruiter.setRole(UserRole.RECRUITER);
        recruiter.setVoteWeight(voteWeight);
        return recruiter;
    }

    private UserProfessional professional(UUID id, int voteWeight) {
        UserProfessional professional = new UserProfessional();
        professional.setId(id);
        professional.setRole(UserRole.PROFESSIONAL);
        professional.setVoteWeight(voteWeight);
        return professional;
    }
}
