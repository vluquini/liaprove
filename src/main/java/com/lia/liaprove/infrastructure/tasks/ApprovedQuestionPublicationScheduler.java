package com.lia.liaprove.infrastructure.tasks;

import com.lia.liaprove.core.usecases.question.PublishApprovedQuestionsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Scheduler responsável por publicar questões aprovadas e liberá-las para avaliações.
 */
@Component
public class ApprovedQuestionPublicationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ApprovedQuestionPublicationScheduler.class);

    private final PublishApprovedQuestionsUseCase publishApprovedQuestionsUseCase;
    private final Duration publicationDelay;

    public ApprovedQuestionPublicationScheduler(
            PublishApprovedQuestionsUseCase publishApprovedQuestionsUseCase,
            @Value("${question.publication.delay:P7D}") Duration publicationDelay) {
        this.publishApprovedQuestionsUseCase = publishApprovedQuestionsUseCase;
        this.publicationDelay = publicationDelay;
    }

    @Scheduled(fixedRateString = "${question.publication.scheduler.fixed-rate-ms:300000}")
    public void publishApprovedQuestions() {
        int publishedCount = publishApprovedQuestionsUseCase.publishEligibleQuestions(publicationDelay, LocalDateTime.now());
        logger.info("Published {} approved questions", publishedCount);
    }
}
