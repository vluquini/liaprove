package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.domain.metrics.Vote;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.usecases.metrics.ListFeedbacksForQuestionUseCase;
import com.lia.liaprove.core.usecases.metrics.ListVotesForQuestionUseCase;
import com.lia.liaprove.core.usecases.question.GetQuestionByIdUseCase;
import com.lia.liaprove.core.usecases.question.ListQuestionsUseCase;
import com.lia.liaprove.core.usecases.question.ModerateQuestionUseCase;
import com.lia.liaprove.core.usecases.question.UpdateQuestionUseCase;
import com.lia.liaprove.infrastructure.dtos.metrics.FeedbackQuestionResponse;
import com.lia.liaprove.infrastructure.dtos.metrics.VoteResponseDto;
import com.lia.liaprove.infrastructure.dtos.question.ModerateQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.QuestionResponse;
import com.lia.liaprove.infrastructure.dtos.question.UpdateQuestionRequest;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackQuestionMapper;
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
@RequestMapping("/api/v1/admin/questions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuestionController {

    private final ListQuestionsUseCase listQuestionsUseCase;
    private final GetQuestionByIdUseCase getQuestionByIdUseCase;
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final ModerateQuestionUseCase moderateQuestionUseCase;
    private final ListVotesForQuestionUseCase listVotesForQuestionUseCase;
    private final ListFeedbacksForQuestionUseCase listFeedbacksForQuestionUseCase;
    private final QuestionMapper questionMapper;
    private final FeedbackQuestionMapper feedbackQuestionMapper;
    private final VoteMapper voteMapper;

    // Question Domain

    @GetMapping
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
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable UUID questionId) {
        Question question = getQuestionByIdUseCase.execute(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        QuestionResponse responseDto = questionMapper.toResponseDto(question);
        return ResponseEntity.ok(responseDto);
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

    @PatchMapping("/{questionId}/moderate")
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

    // Metrics Domain - Votes

    // TODO: Consider creating a separate public endpoint (e.g., /questions/{questionId}/vote-summary)
    // that returns only aggregate vote counts (e.g., total approves, total rejects) for non-admin users.
    @GetMapping("/{questionId}/votes")
    public ResponseEntity<List<VoteResponseDto>> listVotesForQuestion(@PathVariable UUID questionId) {
        List<Vote> votes = listVotesForQuestionUseCase.listVotesForQuestion(questionId);
        List<VoteResponseDto> response = votes.stream()
                .map(voteMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Metrics Domain - Feedbacks

    @GetMapping("/{questionId}/feedbacks")
    public ResponseEntity<List<FeedbackQuestionResponse>> listFeedbacksForQuestion(@PathVariable UUID questionId) {
        List<FeedbackQuestion> feedbacks = listFeedbacksForQuestionUseCase.listFeedbacksForQuestion(questionId);
        List<FeedbackQuestionResponse> response = feedbackQuestionMapper.toResponseDto(feedbacks);
        return ResponseEntity.ok(response);
    }
}
