package com.lia.liaprove.infrastructure.dtos.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteSummaryResponse {
    private long approves;
    private long rejects;
}
