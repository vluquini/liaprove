package com.lia.liaprove.core.usecases.user.admin;

import java.util.UUID;

public interface SetUserVoteWeightUseCase {
    void setVoteWeight(UUID recruiterId, int newWeight, UUID adminId);
}
