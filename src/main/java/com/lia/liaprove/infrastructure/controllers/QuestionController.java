package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.application.services.question.QuestionCreateDto;
import com.lia.liaprove.application.services.question.QuestionVotingDetails;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.usecases.question.*;
import com.lia.liaprove.infrastructure.dtos.question.PreAnalyzeQuestionResponse;
import com.lia.liaprove.infrastructure.dtos.question.QuestionDetailResponse;
import com.lia.liaprove.infrastructure.dtos.question.SubmitQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.SubmitProjectQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.QuestionResponse;
import com.lia.liaprove.infrastructure.dtos.question.QuestionSummaryResponse;
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
    private final GetQuestionVotingDetailsUseCase getQuestionVotingDetailsUseCase;
    private final PreAnalyzeQuestionUseCase preAnalyzeQuestionUseCase;
    private final PrepareQuestionSubmissionUseCase prepareQuestionSubmissionUseCase;
    private final SecurityContextService securityContextService;

    @PostMapping
    public ResponseEntity<QuestionResponse> submitQuestion(@Valid @RequestBody SubmitQuestionRequest request) {
        UUID authorId = securityContextService.getCurrentUserId();
        QuestionCreateDto mappedDto = questionMapper.toQuestionCreateDto(request, authorId);

        PrepareQuestionSubmissionUseCase.PreparationCommand preparationCommand =
                new PrepareQuestionSubmissionUseCase.PreparationCommand(
                        mappedDto.title(),
                        mappedDto.description(),
                        mappedDto.knowledgeAreas(),
                        mappedDto.difficultyByCommunity(),
                        mappedDto.relevanceByCommunity(),
                        toAlternativeInputs(mappedDto.alternatives()),
                        safeList(request.getAcceptedLanguageSuggestions()),
                        safeList(request.getAcceptedBiasOrAmbiguityWarnings()),
                        safeList(request.getAcceptedDistractorSuggestions()),
                        request.getAcceptedDifficultyLevelByLLM(),
                        safeList(request.getAcceptedTopicConsistencyNotes())
                );

        PrepareQuestionSubmissionUseCase.PreparedQuestion preparedQuestion =
                prepareQuestionSubmissionUseCase.execute(preparationCommand);

        QuestionCreateDto dto = new QuestionCreateDto(
                authorId,
                preparedQuestion.title(),
                preparedQuestion.description(),
                mappedDto.knowledgeAreas(),
                mappedDto.difficultyByCommunity(),
                mappedDto.relevanceByCommunity(),
                preparedQuestion.relevanceByLLM(),
                request instanceof SubmitProjectQuestionRequest
                        ? List.of()
                        : toAlternatives(preparedQuestion.alternatives())
        );

        Question submitted = submitQuestionUseCase.submit(dto);

        QuestionResponse responseDto = questionMapper.toResponseDto(submitted);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/pre-analysis")
    public ResponseEntity<PreAnalyzeQuestionResponse> preAnalyzeQuestion(@Valid @RequestBody SubmitQuestionRequest request) {
        UUID authorId = securityContextService.getCurrentUserId();
        QuestionCreateDto dto = questionMapper.toQuestionCreateDto(request, authorId);

        List<String> alternativesText = dto.alternatives() == null
                ? List.of()
                : dto.alternatives().stream().map(Alternative::text).toList();

        PreAnalyzeQuestionUseCase.PreAnalysisCommand command = new PreAnalyzeQuestionUseCase.PreAnalysisCommand(
                dto.title(),
                dto.description(),
                dto.knowledgeAreas(),
                dto.difficultyByCommunity(),
                dto.relevanceByCommunity(),
                alternativesText
        );

        PreAnalyzeQuestionUseCase.PreAnalysisResult result = preAnalyzeQuestionUseCase.execute(command);

        PreAnalyzeQuestionResponse response = new PreAnalyzeQuestionResponse(
                result.languageSuggestions(),
                result.biasOrAmbiguityWarnings(),
                result.distractorSuggestions(),
                result.difficultyLevelByLLM(),
                result.topicConsistencyNotes()
        );

        return ResponseEntity.ok(response);
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

    private static List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }

    private static List<PrepareQuestionSubmissionUseCase.AlternativeInput> toAlternativeInputs(List<Alternative> alternatives) {
        if (alternatives == null) {
            return List.of();
        }
        return alternatives.stream()
                .map(alt -> new PrepareQuestionSubmissionUseCase.AlternativeInput(alt.text(), alt.correct()))
                .toList();
    }

    private static List<Alternative> toAlternatives(List<PrepareQuestionSubmissionUseCase.AlternativeInput> alternatives) {
        if (alternatives == null) {
            return List.of();
        }
        return alternatives.stream()
                .map(alt -> new Alternative(null, alt.text(), alt.correct()))
                .toList();
    }
}
