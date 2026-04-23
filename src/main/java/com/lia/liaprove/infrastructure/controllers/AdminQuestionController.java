package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.usecases.question.GetQuestionByIdUseCase;
import com.lia.liaprove.core.usecases.question.ListQuestionsUseCase;
import com.lia.liaprove.core.usecases.question.ModerateQuestionUseCase;
import com.lia.liaprove.core.usecases.question.UpdateQuestionUseCase;
import com.lia.liaprove.infrastructure.dtos.question.ModerateQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.QuestionResponse;
import com.lia.liaprove.infrastructure.dtos.question.UpdateQuestionRequest;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.security.SecurityContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    private final QuestionMapper questionMapper;
    private final SecurityContextService securityContextService;

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
        UUID actorId = securityContextService.getCurrentUserId();

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
                new ModerateQuestionUseCase.ModerateQuestionCommand(request.newStatus());

        Question moderatedQuestion = moderateQuestionUseCase.execute(questionId, command)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        QuestionResponse responseDto = questionMapper.toResponseDto(moderatedQuestion);
        return ResponseEntity.ok(responseDto);
    }

}
