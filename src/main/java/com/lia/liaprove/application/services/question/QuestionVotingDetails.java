package com.lia.liaprove.application.services.question;

import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;

import java.util.List;

/**
 * Record utilizado para fornecer informações relevantes
 * de questões em fase de votação.
 */
public record QuestionVotingDetails(
    Question question,
    User author,
    long approveVotes,
    long rejectVotes,
    List<FeedbackQuestion> feedbacks
) {}
