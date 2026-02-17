package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.usecases.metrics.CastVoteUseCase;
import com.lia.liaprove.core.usecases.metrics.ReactToFeedbackUseCase;
import com.lia.liaprove.core.usecases.metrics.SubmitFeedbackOnQuestionUseCase;
import com.lia.liaprove.core.usecases.metrics.UpdateFeedbackCommentUseCase;
import com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.ReactToFeedbackRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.SubmitFeedbackQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.UpdateFeedbackCommentRequest;
import com.lia.liaprove.infrastructure.security.SecurityContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class MetricsController {

    private final SubmitFeedbackOnQuestionUseCase submitFeedbackOnQuestionUseCase;
    private final CastVoteUseCase castVoteUseCase;
    private final ReactToFeedbackUseCase reactToFeedbackUseCase;
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

    @PatchMapping("/feedbacks/{feedbackId}")
    public ResponseEntity<Void> updateFeedbackComment(@PathVariable UUID feedbackId,
                                                      @Valid @RequestBody UpdateFeedbackCommentRequest request) {
        UUID actorId = securityContextService.getCurrentUserId();

        updateFeedbackCommentUseCase.execute(actorId, feedbackId, request.getComment());
        return ResponseEntity.ok().build();
    }
}
