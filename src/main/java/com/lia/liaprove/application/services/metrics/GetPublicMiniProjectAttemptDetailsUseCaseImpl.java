package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.metrics.AssessmentAttemptVoteGateway;
import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.metrics.AssessmentAttemptVote;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.core.usecases.metrics.GetPublicMiniProjectAttemptDetailsUseCase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetPublicMiniProjectAttemptDetailsUseCaseImpl implements GetPublicMiniProjectAttemptDetailsUseCase {

    private final AssessmentAttemptGateway assessmentAttemptGateway;
    private final AssessmentAttemptVoteGateway assessmentAttemptVoteGateway;
    private final FeedbackGateway feedbackGateway;

    public GetPublicMiniProjectAttemptDetailsUseCaseImpl(AssessmentAttemptGateway assessmentAttemptGateway,
                                                         AssessmentAttemptVoteGateway assessmentAttemptVoteGateway,
                                                         FeedbackGateway feedbackGateway) {
        this.assessmentAttemptGateway = assessmentAttemptGateway;
        this.assessmentAttemptVoteGateway = assessmentAttemptVoteGateway;
        this.feedbackGateway = feedbackGateway;
    }

    @Override
    public Optional<PublicMiniProjectAttemptDetails> execute(UUID attemptId, UUID currentUserId) {
        return assessmentAttemptGateway.findPublicSystemProjectAttemptDetailsExcludingUser(attemptId, currentUserId)
                .flatMap(this::toDetails);
    }

    private Optional<PublicMiniProjectAttemptDetails> toDetails(AssessmentAttempt attempt) {
        if (attempt.getAnswers() == null || attempt.getQuestions() == null) {
            return Optional.empty();
        }

        for (Answer answer : attempt.getAnswers()) {
            if (!hasProjectSubmission(answer)) {
                continue;
            }

            Optional<ProjectQuestion> question = findProjectQuestion(attempt, answer.getQuestionId());
            if (question.isPresent()) {
                List<AssessmentAttemptVote> votes = assessmentAttemptVoteGateway.findByAttemptId(attempt.getId());
                if (votes == null) {
                    votes = List.of();
                }

                return Optional.of(new PublicMiniProjectAttemptDetails(
                        attempt,
                        question.get(),
                        blankToNull(answer.getProjectUrl()),
                        blankToNull(answer.getTextResponse()),
                        countVotes(votes, VoteType.APPROVE),
                        countVotes(votes, VoteType.REJECT),
                        Optional.ofNullable(feedbackGateway.findAssessmentFeedbacksByAttemptId(attempt.getId()))
                                .orElse(List.of())
                ));
            }
        }

        return Optional.empty();
    }

    private Optional<ProjectQuestion> findProjectQuestion(AssessmentAttempt attempt, UUID questionId) {
        return attempt.getQuestions().stream()
                .filter(question -> question.getId().equals(questionId))
                .filter(ProjectQuestion.class::isInstance)
                .map(ProjectQuestion.class::cast)
                .findFirst();
    }

    private boolean hasProjectSubmission(Answer answer) {
        return answer != null && (hasText(answer.getProjectUrl()) || hasText(answer.getTextResponse()));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String blankToNull(String value) {
        return hasText(value) ? value : null;
    }

    private long countVotes(List<AssessmentAttemptVote> votes, VoteType voteType) {
        return votes.stream()
                .map(AssessmentAttemptVote::getVoteType)
                .filter(voteType::equals)
                .count();
    }
}
