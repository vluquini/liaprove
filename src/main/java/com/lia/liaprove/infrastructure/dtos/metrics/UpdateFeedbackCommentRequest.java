package com.lia.liaprove.infrastructure.dtos.metrics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeedbackCommentRequest {
    @NotBlank(message = "Comment cannot be blank")
    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    private String comment;
}
