package com.lia.liaprove.infrastructure.dtos.question;

import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.dtos.AuthorDto;
import com.lia.liaprove.infrastructure.dtos.metrics.FeedbackQuestionResponse;
import com.lia.liaprove.infrastructure.dtos.metrics.VoteSummaryResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDetailResponse {
    // Summary fields
    private UUID id;
    private AuthorDto author;
    private String title;
    private Set<KnowledgeArea> knowledgeAreas;
    private LocalDateTime submissionDate;

    // Detail fields
    private String description;
    private List<AlternativeDto> alternatives;
    private VoteSummaryResponse voteSummary;
    private List<FeedbackQuestionResponse> feedbacks;
    private RelevanceLevel relevanceByLLM;
}
