package com.lia.liaprove.infrastructure.dtos.question;

import com.lia.liaprove.core.domain.question.KnowledgeArea;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSummaryResponse {
    private UUID id;
    private UUID authorId;
    private String title;
    private Set<KnowledgeArea> knowledgeAreas;
    private LocalDateTime submissionDate;
}
