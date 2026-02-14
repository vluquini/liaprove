package com.lia.liaprove.infrastructure.dtos.metrics;

import com.lia.liaprove.infrastructure.dtos.user.AuthorDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackQuestionResponse {
    private UUID id;
    private String comment;
    private AuthorDto author;
    private LocalDateTime submissionDate;
    private List<FeedbackReactionResponse> reactions;
}
