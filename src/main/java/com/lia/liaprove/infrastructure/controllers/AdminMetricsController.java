package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.domain.metrics.Vote;
import com.lia.liaprove.core.usecases.metrics.ListFeedbacksForQuestionUseCase;
import com.lia.liaprove.core.usecases.metrics.ListVotesForQuestionUseCase;
import com.lia.liaprove.infrastructure.dtos.metrics.FeedbackQuestionResponse;
import com.lia.liaprove.infrastructure.dtos.metrics.VoteResponseDto;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackQuestionMapper;
import com.lia.liaprove.infrastructure.mappers.metrics.VoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/questions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMetricsController {

    private final ListVotesForQuestionUseCase listVotesForQuestionUseCase;
    private final ListFeedbacksForQuestionUseCase listFeedbacksForQuestionUseCase;
    private final FeedbackQuestionMapper feedbackQuestionMapper;
    private final VoteMapper voteMapper;

    // Metrics Domain - Votes

    @GetMapping("/{questionId}/votes")
    public ResponseEntity<List<VoteResponseDto>> listVotesForQuestion(@PathVariable UUID questionId) {
        // The use case already handles validation for question existence
        List<Vote> votes = listVotesForQuestionUseCase.listVotesForQuestion(questionId);
        List<VoteResponseDto> response = votes.stream()
                .map(voteMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Metrics Domain - Feedbacks

    @GetMapping("/{questionId}/feedbacks")
    public ResponseEntity<List<FeedbackQuestionResponse>> listFeedbacksForQuestion(@PathVariable UUID questionId) {
        // The use case already handles validation for question existence
        List<FeedbackQuestion> feedbacks = listFeedbacksForQuestionUseCase.listFeedbacksForQuestion(questionId);
        List<FeedbackQuestionResponse> response = feedbackQuestionMapper.toResponseDto(feedbacks);
        return ResponseEntity.ok(response);
    }
}
