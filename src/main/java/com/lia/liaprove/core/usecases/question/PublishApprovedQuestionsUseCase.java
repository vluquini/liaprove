package com.lia.liaprove.core.usecases.question;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Caso de uso responsável por publicar questões aprovadas quando cumprirem o tempo de espera configurado.
 */
public interface PublishApprovedQuestionsUseCase {
    int publishEligibleQuestions(Duration publicationDelay, LocalDateTime now);
}
