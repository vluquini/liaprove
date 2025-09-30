package com.lia.liaprove.core.domain.metrics;

import java.time.LocalDateTime;

/*
Esta classe (provavelmente) será utilizada para centralizar o cálculo
de peso dos usuários Professional e Recruiter.
 */
public class VoteWeightMetrics {
    private Float recruiterWeightBase;
    private Float professionalWeightBase;
    private LocalDateTime lastUpdate;
}
