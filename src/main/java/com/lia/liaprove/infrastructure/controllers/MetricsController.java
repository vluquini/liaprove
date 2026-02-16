package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.usecases.metrics.CastVoteUseCase;
import com.lia.liaprove.core.usecases.metrics.ReactToFeedbackUseCase;
import com.lia.liaprove.core.usecases.metrics.SubmitFeedbackOnQuestionUseCase;
import com.lia.liaprove.core.usecases.metrics.UpdateFeedbackCommentUseCase;
import com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.ReactToFeedbackRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.SubmitFeedbackQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.UpdateFeedbackCommentRequest;
import com.lia.liaprove.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/questions/{questionId}/vote")
    public ResponseEntity<Void> castVote(@PathVariable UUID questionId, @Valid @RequestBody CastVoteRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID userId = principal.user().getId();

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID userId = principal.user().getId();

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID userId = principal.user().getId();

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID actorId = principal.user().getId();

        updateFeedbackCommentUseCase.execute(actorId, feedbackId, request.getComment());
        return ResponseEntity.ok().build();
    }
}
