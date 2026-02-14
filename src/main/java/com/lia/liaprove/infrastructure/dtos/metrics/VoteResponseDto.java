package com.lia.liaprove.infrastructure.dtos.metrics;

import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponseDto {
    private UUID id;
    private UserResponseDto user;
    private VoteType voteType;
    private LocalDateTime createdAt;
}
