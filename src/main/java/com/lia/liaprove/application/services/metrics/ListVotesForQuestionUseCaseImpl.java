package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.metrics.VoteGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.metrics.Vote;
import com.lia.liaprove.core.exceptions.QuestionNotFoundException;
import com.lia.liaprove.core.usecases.metrics.ListVotesForQuestionUseCase;

import java.util.List;
import java.util.UUID;

public class ListVotesForQuestionUseCaseImpl implements ListVotesForQuestionUseCase {

    private final VoteGateway voteGateway;
    private final QuestionGateway questionGateway;

    public ListVotesForQuestionUseCaseImpl(VoteGateway voteGateway, QuestionGateway questionGateway) {
        this.voteGateway = voteGateway;
        this.questionGateway = questionGateway;
    }

    @Override
    public List<Vote> listVotesForQuestion(UUID questionId) {
        // First, ensure the question exists.
        questionGateway.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + questionId));

        // Then, retrieve the votes for that question.
        return voteGateway.findVotesByQuestionId(questionId);
    }
}
