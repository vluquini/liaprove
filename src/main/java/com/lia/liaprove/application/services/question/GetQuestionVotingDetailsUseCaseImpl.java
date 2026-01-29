package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.application.gateways.metrics.VoteGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.domain.metrics.Vote;
import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.usecases.question.GetQuestionVotingDetailsUseCase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do use case para listar informações de
 * questões em fase de votação.
 */
public class GetQuestionVotingDetailsUseCaseImpl implements GetQuestionVotingDetailsUseCase {

    private final QuestionGateway questionGateway;
    private final VoteGateway voteGateway;
    private final FeedbackGateway feedbackGateway;
    private final UserGateway userGateway;

    public GetQuestionVotingDetailsUseCaseImpl(QuestionGateway questionGateway, VoteGateway voteGateway,
                                               FeedbackGateway feedbackGateway, UserGateway userGateway) {
        this.questionGateway = questionGateway;
        this.voteGateway = voteGateway;
        this.feedbackGateway = feedbackGateway;
        this.userGateway = userGateway;
    }

    @Override
    public Optional<QuestionVotingDetails> execute(UUID questionId) {
        Optional<Question> questionOpt = questionGateway.findById(questionId);
        if (questionOpt.isEmpty()) {
            return Optional.empty();
        }
        Question question = questionOpt.get();

        // Fetch the author User object
        User author = userGateway.findById(question.getAuthorId())
                .orElseThrow(() -> new IllegalStateException("Question with id " + question.getId() + " has an invalid authorId."));


        List<Vote> votes = voteGateway.findVotesByQuestionId(questionId);
        List<FeedbackQuestion> feedbacks = feedbackGateway.findFeedbacksByQuestionId(questionId);

        long approveVotes = votes.stream().filter(vote -> vote.getVoteType() == VoteType.APPROVE).count();
        long rejectVotes = votes.stream().filter(vote -> vote.getVoteType() == VoteType.REJECT).count();

        QuestionVotingDetails details = new QuestionVotingDetails(question, author, approveVotes, rejectVotes, feedbacks);
        return Optional.of(details);
    }
}
