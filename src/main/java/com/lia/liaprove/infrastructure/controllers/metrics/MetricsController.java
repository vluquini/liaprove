package com.lia.liaprove.infrastructure.controllers.metrics;

import com.lia.liaprove.core.usecases.metrics.SubmitFeedbackOnAssessmentUseCase;
import com.lia.liaprove.core.usecases.metrics.CastVoteOnAssessmentAttemptUseCase;
import com.lia.liaprove.core.usecases.metrics.CastVoteUseCase;
import com.lia.liaprove.core.usecases.metrics.GetPublicMiniProjectAttemptDetailsUseCase;
import com.lia.liaprove.core.usecases.metrics.ListPublicMiniProjectAttemptsUseCase;
import com.lia.liaprove.core.usecases.metrics.ReactToAssessmentFeedbackUseCase;
import com.lia.liaprove.core.usecases.metrics.ReactToFeedbackUseCase;
import com.lia.liaprove.core.usecases.metrics.SubmitFeedbackOnQuestionUseCase;
import com.lia.liaprove.core.usecases.metrics.UpdateFeedbackCommentUseCase;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.metrics.FeedbackReaction;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.application.services.metrics.PublicMiniProjectAttemptDetails;
import com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.FeedbackAssessmentResponse;
import com.lia.liaprove.infrastructure.dtos.metrics.FeedbackReactionResponse;
import com.lia.liaprove.infrastructure.dtos.metrics.PublicMiniProjectAttemptDetailResponse;
import com.lia.liaprove.infrastructure.dtos.metrics.PublicMiniProjectAttemptResponse;
import com.lia.liaprove.infrastructure.dtos.metrics.PublicMiniProjectQuestionResponse;
import com.lia.liaprove.infrastructure.dtos.metrics.ReactToFeedbackRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.SubmitFeedbackOnAssessmentRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.SubmitFeedbackQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.UpdateFeedbackCommentRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.VoteSummaryResponse;
import com.lia.liaprove.infrastructure.dtos.user.AuthorDto;
import com.lia.liaprove.infrastructure.security.SecurityContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class MetricsController {

    private final SubmitFeedbackOnQuestionUseCase submitFeedbackOnQuestionUseCase;
    private final SubmitFeedbackOnAssessmentUseCase submitFeedbackOnAssessmentUseCase;
    private final CastVoteUseCase castVoteUseCase;
    private final CastVoteOnAssessmentAttemptUseCase castVoteOnAssessmentAttemptUseCase;
    private final ListPublicMiniProjectAttemptsUseCase listPublicMiniProjectAttemptsUseCase;
    private final GetPublicMiniProjectAttemptDetailsUseCase getPublicMiniProjectAttemptDetailsUseCase;
    private final ReactToFeedbackUseCase reactToFeedbackUseCase;
    private final ReactToAssessmentFeedbackUseCase reactToAssessmentFeedbackUseCase;
    private final UpdateFeedbackCommentUseCase updateFeedbackCommentUseCase;
    private final SecurityContextService securityContextService;

    @PostMapping("/questions/{questionId}/vote")
    public ResponseEntity<Void> castVote(@PathVariable UUID questionId, @Valid @RequestBody CastVoteRequest request) {
        UUID userId = securityContextService.getCurrentUserId();

        castVoteUseCase.castVote(
                userId,
                questionId,
                request.getVoteType()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/assessment-attempts/mini-project/public")
    public ResponseEntity<List<PublicMiniProjectAttemptResponse>> listPublicMiniProjectAttempts() {
        UUID userId = securityContextService.getCurrentUserId();

        List<PublicMiniProjectAttemptResponse> response = listPublicMiniProjectAttemptsUseCase.list(userId).stream()
                .map(this::toPublicMiniProjectAttemptResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/assessment-attempts/mini-project/public/{attemptId}")
    public ResponseEntity<PublicMiniProjectAttemptDetailResponse> getPublicMiniProjectAttemptDetails(
            @PathVariable UUID attemptId) {
        UUID userId = securityContextService.getCurrentUserId();

        PublicMiniProjectAttemptDetails details = getPublicMiniProjectAttemptDetailsUseCase.execute(attemptId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Public mini-project attempt not found"
                ));

        return ResponseEntity.ok(toPublicMiniProjectAttemptDetailResponse(details));
    }

    @PostMapping("/assessment-attempts/{attemptId}/vote")
    public ResponseEntity<Void> castVoteOnAssessmentAttempt(@PathVariable UUID attemptId,
                                                            @Valid @RequestBody CastVoteRequest request) {
        UUID userId = securityContextService.getCurrentUserId();

        castVoteOnAssessmentAttemptUseCase.castVote(userId, attemptId, request.getVoteType());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{questionId}/feedback")
    public ResponseEntity<Void> submitFeedbackOnQuestion(@PathVariable UUID questionId,
                                                         @Valid @RequestBody SubmitFeedbackQuestionRequest request) {
        UUID userId = securityContextService.getCurrentUserId();

        submitFeedbackOnQuestionUseCase.submitFeedback(
                userId,
                questionId,
                request.getComment(),
                request.getDifficultyLevel(),
                request.getKnowledgeArea(),
                request.getRelevanceLevel()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/assessment-attempts/{attemptId}/feedback")
    public ResponseEntity<Void> submitFeedbackOnAssessment(@PathVariable UUID attemptId,
                                                           @Valid @RequestBody SubmitFeedbackOnAssessmentRequest request) {
        UUID userId = securityContextService.getCurrentUserId();

        submitFeedbackOnAssessmentUseCase.submitFeedback(
                userId,
                attemptId,
                request.comment()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/feedbacks/{feedbackId}/react")
    public ResponseEntity<Void> reactToFeedback(@PathVariable UUID feedbackId,
                                                @Valid @RequestBody ReactToFeedbackRequest request) {
        UUID userId = securityContextService.getCurrentUserId();

        reactToFeedbackUseCase.reactToFeedback(
                userId,
                feedbackId,
                request.getReactionType()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assessment-feedbacks/{feedbackId}/react")
    public ResponseEntity<Void> reactToAssessmentFeedback(@PathVariable UUID feedbackId,
                                                          @Valid @RequestBody ReactToFeedbackRequest request) {
        UUID userId = securityContextService.getCurrentUserId();

        reactToAssessmentFeedbackUseCase.reactToFeedback(
                userId,
                feedbackId,
                request.getReactionType()
        );
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/feedbacks/{feedbackId}")
    public ResponseEntity<Void> updateFeedbackComment(@PathVariable UUID feedbackId,
                                                      @Valid @RequestBody UpdateFeedbackCommentRequest request) {
        UUID actorId = securityContextService.getCurrentUserId();

        updateFeedbackCommentUseCase.execute(actorId, feedbackId, request.getComment());
        return ResponseEntity.ok().build();
    }

    private PublicMiniProjectAttemptResponse toPublicMiniProjectAttemptResponse(AssessmentAttempt attempt) {
        String authorName = attempt.getUser() != null ? attempt.getUser().getName() : null;
        String repositoryLink = attempt.getAnswers() == null ? null : attempt.getAnswers().stream()
                .map(answer -> answer.getProjectUrl())
                .filter(url -> url != null && !url.isBlank())
                .findFirst()
                .orElse(null);

        return new PublicMiniProjectAttemptResponse(
                attempt.getId(),
                attempt.getAssessment() != null ? attempt.getAssessment().getTitle() : null,
                authorName,
                repositoryLink,
                attempt.getFinishedAt()
        );
    }

    private PublicMiniProjectAttemptDetailResponse toPublicMiniProjectAttemptDetailResponse(
            PublicMiniProjectAttemptDetails details) {
        AssessmentAttempt attempt = details.attempt();
        String authorName = attempt.getUser() != null ? attempt.getUser().getName() : null;

        return new PublicMiniProjectAttemptDetailResponse(
                attempt.getId(),
                attempt.getAssessment() != null ? attempt.getAssessment().getTitle() : null,
                authorName,
                attempt.getFinishedAt(),
                details.repositoryLink(),
                details.textResponse(),
                toPublicMiniProjectQuestionResponse(details.question()),
                new VoteSummaryResponse(details.approveVotes(), details.rejectVotes()),
                details.feedbacks().stream()
                        .map(this::toFeedbackAssessmentResponse)
                        .toList()
        );
    }

    private PublicMiniProjectQuestionResponse toPublicMiniProjectQuestionResponse(ProjectQuestion question) {
        return new PublicMiniProjectQuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getDescription(),
                question.getKnowledgeAreas(),
                question.getDifficultyByCommunity(),
                question.getRelevanceByCommunity()
        );
    }

    private FeedbackAssessmentResponse toFeedbackAssessmentResponse(FeedbackAssessment feedback) {
        AuthorDto author = feedback.getUser() == null
                ? null
                : new AuthorDto(feedback.getUser().getId(), feedback.getUser().getName());

        return new FeedbackAssessmentResponse(
                feedback.getId(),
                feedback.getComment(),
                author,
                feedback.getSubmissionDate(),
                feedback.getReactions().stream()
                        .map(this::toFeedbackReactionResponse)
                        .toList()
        );
    }

    private FeedbackReactionResponse toFeedbackReactionResponse(
            FeedbackReaction reaction) {
        return new FeedbackReactionResponse(
                reaction.getId(),
                reaction.getUser() != null ? reaction.getUser().getId() : null,
                reaction.getUser() != null ? reaction.getUser().getName() : null,
                reaction.getType(),
                reaction.getCreatedAt()
        );
    }
}
