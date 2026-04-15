package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.application.services.assessment.dto.SubmitAssessmentAnswersDto;
import com.lia.liaprove.application.services.assessment.dto.SuggestionCriteriaDto;
import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.usecases.assessments.CreatePersonalizedAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.DeletePersonalizedAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.EvaluateAssessmentAttemptUseCase;
import com.lia.liaprove.core.usecases.assessments.GetAssessmentAttemptDetailsUseCase;
import com.lia.liaprove.core.usecases.assessments.ListAttemptsForMyAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.StartNewAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.SubmitAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.SuggestQuestionsForAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.UpdatePersonalizedAssessmentUseCase;
import com.lia.liaprove.infrastructure.dtos.assessment.*;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentDtoMapper;
import com.lia.liaprove.infrastructure.security.SecurityContextService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/assessments")
public class AssessmentController {

    private final StartNewAssessmentUseCase startNewAssessmentUseCase;
    private final SubmitAssessmentUseCase submitAssessmentUseCase;
    private final CreatePersonalizedAssessmentUseCase createPersonalizedAssessmentUseCase;
    private final SuggestQuestionsForAssessmentUseCase suggestQuestionsForAssessmentUseCase;
    private final EvaluateAssessmentAttemptUseCase evaluateAssessmentAttemptUseCase;
    private final DeletePersonalizedAssessmentUseCase deletePersonalizedAssessmentUseCase;
    private final GetAssessmentAttemptDetailsUseCase getAssessmentAttemptDetailsUseCase;
    private final ListAttemptsForMyAssessmentUseCase listAttemptsForMyAssessmentUseCase;
    private final UpdatePersonalizedAssessmentUseCase updatePersonalizedAssessmentUseCase;
    private final SecurityContextService securityContextService;
    private final AssessmentDtoMapper assessmentDtoMapper;

    public AssessmentController(StartNewAssessmentUseCase startNewAssessmentUseCase,
                                SubmitAssessmentUseCase submitAssessmentUseCase,
                                CreatePersonalizedAssessmentUseCase createPersonalizedAssessmentUseCase,
                                SuggestQuestionsForAssessmentUseCase suggestQuestionsForAssessmentUseCase,
                                EvaluateAssessmentAttemptUseCase evaluateAssessmentAttemptUseCase,
                                DeletePersonalizedAssessmentUseCase deletePersonalizedAssessmentUseCase,
                                GetAssessmentAttemptDetailsUseCase getAssessmentAttemptDetailsUseCase,
                                ListAttemptsForMyAssessmentUseCase listAttemptsForMyAssessmentUseCase,
                                UpdatePersonalizedAssessmentUseCase updatePersonalizedAssessmentUseCase,
                                SecurityContextService securityContextService,
                                AssessmentDtoMapper assessmentDtoMapper) {
        this.startNewAssessmentUseCase = startNewAssessmentUseCase;
        this.submitAssessmentUseCase = submitAssessmentUseCase;
        this.createPersonalizedAssessmentUseCase = createPersonalizedAssessmentUseCase;
        this.suggestQuestionsForAssessmentUseCase = suggestQuestionsForAssessmentUseCase;
        this.evaluateAssessmentAttemptUseCase = evaluateAssessmentAttemptUseCase;
        this.deletePersonalizedAssessmentUseCase = deletePersonalizedAssessmentUseCase;
        this.getAssessmentAttemptDetailsUseCase = getAssessmentAttemptDetailsUseCase;
        this.listAttemptsForMyAssessmentUseCase = listAttemptsForMyAssessmentUseCase;
        this.updatePersonalizedAssessmentUseCase = updatePersonalizedAssessmentUseCase;
        this.securityContextService = securityContextService;
        this.assessmentDtoMapper = assessmentDtoMapper;
    }

    @PostMapping("/start-system")
    public ResponseEntity<AssessmentAttemptResponse> startSystemAssessment(
            @RequestBody @Valid StartSystemAssessmentRequest request) {
        
        UUID userId = securityContextService.getCurrentUserId();

        AssessmentAttempt attempt = startNewAssessmentUseCase.execute(
                userId,
                null,
                request.knowledgeArea(),
                request.difficultyLevel(),
                request.type()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentDtoMapper.toAttemptResponse(attempt));
    }

    @PostMapping("/start-personalized/{token}")
    public ResponseEntity<AssessmentAttemptResponse> startPersonalizedAssessment(
            @PathVariable String token) {

        UUID userId = securityContextService.getCurrentUserId();

        AssessmentAttempt attempt = startNewAssessmentUseCase.execute(
                userId,
                token,
                null,
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentDtoMapper.toAttemptResponse(attempt));
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<AssessmentResultResponse> submitAssessment(
            @PathVariable UUID attemptId,
            @RequestBody @Valid SubmitAssessmentRequest request) {

        UUID userId = securityContextService.getCurrentUserId();

        List<SubmitAssessmentAnswersDto.QuestionAnswerDto> answersDto = request.answers().stream()
                .map(a -> new SubmitAssessmentAnswersDto.QuestionAnswerDto(
                        a.questionId(),
                        a.selectedAlternativeId(),
                        a.projectUrl(),
                        a.textResponse()
                ))
                .collect(Collectors.toList());

        SubmitAssessmentAnswersDto submitDto = new SubmitAssessmentAnswersDto(attemptId, answersDto);

        AssessmentAttempt finalAttempt = submitAssessmentUseCase.execute(submitDto, userId);

        return ResponseEntity.ok(assessmentDtoMapper.toResultResponse(finalAttempt));
    }

    @PostMapping("/personalized")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<PersonalizedAssessmentResponse> createPersonalizedAssessment(
            @RequestBody @Valid CreatePersonalizedAssessmentRequest request) {

        UUID userId = securityContextService.getCurrentUserId();

        PersonalizedAssessment assessment = createPersonalizedAssessmentUseCase.execute(
                userId,
                request.title(),
                request.description(),
                request.questionIds(),
                request.expirationDate(),
                request.maxAttempts(),
                request.evaluationTimerMinutes()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentDtoMapper.toPersonalizedResponse(assessment));
    }

    @GetMapping("/personalized/suggestions")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<SuggestedQuestionsResponse> getSuggestedQuestions(
            @RequestParam(required = false) Set<KnowledgeArea> knowledgeAreas,
            @RequestParam(required = false) Set<DifficultyLevel> difficultyLevels,
            @RequestParam(required = false) Set<QuestionType> questionTypes,
            @RequestParam(required = false) List<UUID> excludeIds,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        UUID userId = securityContextService.getCurrentUserId();

        SuggestionCriteriaDto criteria = new SuggestionCriteriaDto(
                knowledgeAreas,
                difficultyLevels,
                questionTypes,
                page,
                pageSize,
                excludeIds
        );

        List<ScoredQuestion> suggestions = suggestQuestionsForAssessmentUseCase.execute(userId, criteria);

        List<ScoredQuestionResponse> dtos = suggestions.stream()
                .map(assessmentDtoMapper::toScoredQuestionResponse)
                .collect(Collectors.toList());

        // Assuming the service returns the specific page content, and we don't have total count info separate from the list size
        // for this specific use case (Bayesian suggestion usually limits results).
        // Mimicking previous behavior where total = list size.
        SuggestedQuestionsResponse response = new SuggestedQuestionsResponse(
                dtos,
                page,
                pageSize,
                dtos.size(),
                1,   // totalPages defaults to 1 as we treat the result as the full set
                true // last defaults to true
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{attemptId}/evaluate")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<EvaluateAssessmentAttemptResponse> evaluateAssessmentAttempt(
            @PathVariable UUID attemptId,
            @RequestBody @Valid EvaluateAssessmentAttemptRequest request) {

        UUID requesterId = securityContextService.getCurrentUserId();

        AssessmentAttempt updatedAttempt = evaluateAssessmentAttemptUseCase.execute(
                attemptId,
                requesterId,
                request.finalStatus()
        );

        return ResponseEntity.ok(assessmentDtoMapper.toEvaluateAttemptResponse(updatedAttempt));
    }

    @DeleteMapping("/personalized/{assessmentId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<DeletePersonalizedAssessmentResponse> deletePersonalizedAssessment(
            @PathVariable UUID assessmentId) {

        UUID requesterId = securityContextService.getCurrentUserId();

        deletePersonalizedAssessmentUseCase.execute(assessmentId, requesterId);

        return ResponseEntity.ok(assessmentDtoMapper.toDeleteResponse(assessmentId));
    }

    @GetMapping("/attempts/{attemptId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<AssessmentAttemptDetailsResponse> getAssessmentAttemptDetails(
            @PathVariable UUID attemptId) {

        UUID requesterId = securityContextService.getCurrentUserId();

        AssessmentAttempt attempt = getAssessmentAttemptDetailsUseCase.execute(attemptId, requesterId);

        return ResponseEntity.ok(assessmentDtoMapper.toAttemptDetailsResponse(attempt));
    }

    @GetMapping("/personalized/{assessmentId}/attempts")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<AssessmentAttemptSummaryResponse>> listAttemptsForMyAssessment(
            @PathVariable UUID assessmentId) {

        UUID recruiterId = securityContextService.getCurrentUserId();

        List<AssessmentAttempt> attempts = listAttemptsForMyAssessmentUseCase.execute(assessmentId, recruiterId);
        List<AssessmentAttemptSummaryResponse> response = attempts.stream()
                .map(assessmentDtoMapper::toAttemptSummaryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/personalized/{assessmentId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<UpdatePersonalizedAssessmentResponse> updatePersonalizedAssessment(
            @PathVariable UUID assessmentId,
            @RequestBody @Valid UpdatePersonalizedAssessmentRequest request) {

        UUID requesterId = securityContextService.getCurrentUserId();

        Assessment updated = updatePersonalizedAssessmentUseCase.execute(
                assessmentId,
                requesterId,
                java.util.Optional.ofNullable(request.expirationDate()),
                java.util.Optional.ofNullable(request.maxAttempts()),
                java.util.Optional.ofNullable(request.status())
        );

        return ResponseEntity.ok(
                assessmentDtoMapper.toUpdatePersonalizedResponse((PersonalizedAssessment) updated)
        );
    }
}
