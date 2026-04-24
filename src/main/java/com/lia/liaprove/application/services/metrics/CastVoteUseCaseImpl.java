package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.metrics.VoteGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.metrics.Vote;
import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.question.QuestionNotFoundException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.metrics.CastVoteUseCase;

import java.util.Comparator;
import java.util.List;
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

        List<Vote> existingVotes = voteGateway.findByUserIdAndQuestionId(userId, questionId);
        if (existingVotes.isEmpty()) {
            voteGateway.save(new Vote(user, question, voteType));
            return;
        }

        handleExistingVotes(existingVotes, user, question, voteType);
    }

    private void handleExistingVotes(List<Vote> existingVotes, User user, Question question, VoteType requestedVoteType) {
        Vote latestVote = existingVotes.stream()
                .max(Comparator.comparing(Vote::getCreatedAt))
                .orElseThrow();

        for (Vote existingVote : existingVotes) {
            voteGateway.delete(existingVote);
        }

        if (latestVote.getVoteType() == requestedVoteType) {
            return;
        }

        voteGateway.save(new Vote(user, question, requestedVoteType));
    }
}
