package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.application.services.question.QuestionCreateDto;
import com.lia.liaprove.application.services.question.QuestionVotingDetails;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.usecases.question.*;
import com.lia.liaprove.core.usecases.metrics.CastVoteUseCase;
import com.lia.liaprove.core.usecases.metrics.SubmitFeedbackOnQuestionUseCase;
import com.lia.liaprove.core.usecases.metrics.ReactToFeedbackUseCase;
import com.lia.liaprove.core.usecases.metrics.UpdateFeedbackCommentUseCase;
import com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.SubmitFeedbackQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.ReactToFeedbackRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.UpdateFeedbackCommentRequest;
import com.lia.liaprove.infrastructure.dtos.question.QuestionDetailResponse;
import com.lia.liaprove.infrastructure.dtos.question.SubmitQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.QuestionResponse;
import com.lia.liaprove.infrastructure.dtos.question.QuestionSummaryResponse;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class QuestionController {

    private final SubmitQuestionUseCase submitQuestionUseCase;
    private final ListQuestionsUseCase listQuestionsUseCase;
    private final QuestionMapper questionMapper;
    private final SubmitFeedbackOnQuestionUseCase submitFeedbackOnQuestionUseCase;
    private final CastVoteUseCase castVoteUseCase;
    private final GetQuestionVotingDetailsUseCase getQuestionVotingDetailsUseCase;
    private final ReactToFeedbackUseCase reactToFeedbackUseCase;
    private final UpdateFeedbackCommentUseCase updateFeedbackCommentUseCase;

    @PostMapping
    public ResponseEntity<QuestionResponse> submitQuestion(@Valid @RequestBody SubmitQuestionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID authorId = principal.user().getId();

        QuestionCreateDto dto = questionMapper.toQuestionCreateDto(request, authorId);
        Question submitted = submitQuestionUseCase.submit(dto);

        QuestionResponse responseDto = questionMapper.toResponseDto(submitted);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/voting")
    public ResponseEntity<List<QuestionSummaryResponse>> listVotingQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ListQuestionsUseCase.ListQuestionsQuery query = new ListQuestionsUseCase.ListQuestionsQuery(
                null,                  // knowledgeAreas
                null,                  // difficultyLevel
                QuestionStatus.VOTING, // status
                null,                  // authorId
                page,
                size
        );

        List<Question> questions = listQuestionsUseCase.execute(query);
        List<QuestionSummaryResponse> response = questions.stream()
                .map(questionMapper::toSummaryResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{questionId}/voting-details")
    public ResponseEntity<QuestionDetailResponse> getQuestionVotingDetails(@PathVariable UUID questionId) {
        QuestionVotingDetails details = getQuestionVotingDetailsUseCase.execute(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        QuestionDetailResponse responseDto = questionMapper.toQuestionDetailResponseDto(details);
        return ResponseEntity.ok(responseDto);
    }

    // Metrics Domain - Feedback

    @PostMapping("/{questionId}/feedback")
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
        return ResponseEntity.status(HttpStatus.CREATED).build();
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

    // Metrics Domain - Vote

    @PostMapping("/{questionId}/vote")
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
}

