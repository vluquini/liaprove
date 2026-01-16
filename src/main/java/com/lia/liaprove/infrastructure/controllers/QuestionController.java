package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.application.services.question.QuestionCreateDto;
import com.lia.liaprove.core.domain.metrics.Vote;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.usecases.metrics.ListVotesForQuestionUseCase;
import com.lia.liaprove.core.usecases.question.*;
import com.lia.liaprove.core.usecases.metrics.CastVoteUseCase;
import com.lia.liaprove.core.usecases.metrics.SubmitFeedbackOnQuestionUseCase;
import com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.CreateFeedbackQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.VoteResponseDto;
import com.lia.liaprove.infrastructure.dtos.question.ModerateQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.QuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.QuestionResponse;
import com.lia.liaprove.infrastructure.dtos.question.UpdateQuestionRequest;
import com.lia.liaprove.infrastructure.mappers.metrics.VoteMapper;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final SubmitQuestionUseCase submitQuestionUseCase;
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final ListQuestionsUseCase listQuestionsUseCase;
    private final GetQuestionByIdUseCase getQuestionByIdUseCase;
    private final ModerateQuestionUseCase moderateQuestionUseCase;
    private final QuestionMapper questionMapper;
    private final SubmitFeedbackOnQuestionUseCase submitFeedbackOnQuestionUseCase;
    private final CastVoteUseCase castVoteUseCase;
    private final ListVotesForQuestionUseCase listVotesForQuestionUseCase;
    private final VoteMapper voteMapper;

    @PostMapping
    public ResponseEntity<QuestionResponse> submitQuestion(@Valid @RequestBody QuestionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID authorId = principal.user().getId();

        QuestionCreateDto dto = questionMapper.toQuestionCreateDto(request, authorId);
        Question submitted = submitQuestionUseCase.submit(dto);

        QuestionResponse responseDto = questionMapper.toResponseDto(submitted);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable UUID questionId,
            @RequestBody UpdateQuestionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID actorId = principal.user().getId();

        UpdateQuestionUseCase.UpdateQuestionCommand command = questionMapper.toUpdateCommand(request);

        Question updatedQuestion = updateQuestionUseCase.execute(actorId, questionId, command)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        QuestionResponse responseDto = questionMapper.toResponseDto(updatedQuestion);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/voting")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuestionResponse>> listVotingQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ListQuestionsUseCase.ListQuestionsQuery query = new ListQuestionsUseCase.ListQuestionsQuery(
                null, // knowledgeAreas
                null, // difficultyLevel
                QuestionStatus.VOTING, // status
                null, // authorId
                page,
                size
        );

        List<Question> questions = listQuestionsUseCase.execute(query);
        List<QuestionResponse> response = questions.stream()
                .map(questionMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuestionResponse>> listAllQuestions(
            @RequestParam(required = false) Set<KnowledgeArea> knowledgeAreas,
            @RequestParam(required = false) DifficultyLevel difficultyLevel,
            @RequestParam(required = false) QuestionStatus status,
            @RequestParam(required = false) UUID authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ListQuestionsUseCase.ListQuestionsQuery query = new ListQuestionsUseCase.ListQuestionsQuery(
                knowledgeAreas, difficultyLevel, status, authorId, page, size);

        List<Question> questions = listQuestionsUseCase.execute(query);
        List<QuestionResponse> response = questions.stream()
                .map(questionMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{questionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable UUID questionId) {
        Question question = getQuestionByIdUseCase.execute(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        QuestionResponse responseDto = questionMapper.toResponseDto(question);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{questionId}/moderate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionResponse> moderateQuestion(
            @PathVariable UUID questionId,
            @Valid @RequestBody ModerateQuestionRequest request) {

        ModerateQuestionUseCase.ModerateQuestionCommand command =
                new ModerateQuestionUseCase.ModerateQuestionCommand(request.newStatus(), Optional.empty());

        Question moderatedQuestion = moderateQuestionUseCase.execute(questionId, command)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        QuestionResponse responseDto = questionMapper.toResponseDto(moderatedQuestion);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{questionId}/feedback")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> submitFeedbackOnQuestion(
            @PathVariable UUID questionId,
            @Valid @RequestBody CreateFeedbackQuestionRequest request) {
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

    @PostMapping("/{questionId}/vote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> castVote(
            @PathVariable UUID questionId,
            @Valid @RequestBody CastVoteRequest request) {
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

    @GetMapping("/{questionId}/votes")
    // TODO: Consider creating a separate public endpoint (e.g., /questions/{questionId}/vote-summary)
    // that returns only aggregate vote counts (e.g., total approves, total rejects) for non-admin users.
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VoteResponseDto>> getVotesForQuestion(@PathVariable UUID questionId) {
        List<Vote> votes = listVotesForQuestionUseCase.getVotesForQuestion(questionId);
        List<VoteResponseDto> response = votes.stream()
                .map(voteMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}

