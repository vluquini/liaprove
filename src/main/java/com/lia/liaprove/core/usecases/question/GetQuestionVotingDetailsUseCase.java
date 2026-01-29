package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.application.services.question.QuestionVotingDetails;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case para exibir dados de questões em fase
 * de votação.
 */
public interface GetQuestionVotingDetailsUseCase {
    Optional<QuestionVotingDetails> execute(UUID questionId);
}
