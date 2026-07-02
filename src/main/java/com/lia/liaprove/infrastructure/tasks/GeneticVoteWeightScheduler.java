package com.lia.liaprove.infrastructure.tasks;

import com.lia.liaprove.core.usecases.algorithms.genetic.ManageVoteWeightUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler para atualizar pesos de votos dos usuários Recruiters.
 */
@Component
public class GeneticVoteWeightScheduler {

    private static final Logger logger = LoggerFactory.getLogger(GeneticVoteWeightScheduler.class);

    private final ManageVoteWeightUseCase manageVoteWeightUseCase;

    public GeneticVoteWeightScheduler(ManageVoteWeightUseCase manageVoteWeightUseCase) {
        this.manageVoteWeightUseCase = manageVoteWeightUseCase;
    }

    // Cron configured by ga.scheduler.cron. Demo profiles run every 5 minutes.
    @Scheduled(cron = "${ga.scheduler.cron:0 */5 * * * *}")
    public void adjustRecruiterVoteWeights() {
        logger.info("Starting scheduled genetic vote weight adjustment");
        manageVoteWeightUseCase.adjustAllRecruiterWeights(false);
        logger.info("Finished scheduled genetic vote weight adjustment");
    }
}
