package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.metrics.VoteGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.metrics.Vote;
import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.QuestionNotFoundException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.metrics.CastVoteUseCase;

import java.util.UUID;

public class CastVoteUseCaseImpl implements CastVoteUseCase {

    private final UserGateway userGateway;
    private final QuestionGateway questionGateway;
    private final VoteGateway voteGateway;

    public CastVoteUseCaseImpl(UserGateway userGateway, QuestionGateway questionGateway, VoteGateway voteGateway) {
        this.userGateway = userGateway;
        this.questionGateway = questionGateway;
        this.voteGateway = voteGateway;
    }

    @Override
    public void castVote(UUID userId, UUID questionId, VoteType voteType) {
        User user = userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Question question = questionGateway.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + questionId));

        Vote vote = new Vote(user, question, voteType);

        voteGateway.save(vote);
    }
}
