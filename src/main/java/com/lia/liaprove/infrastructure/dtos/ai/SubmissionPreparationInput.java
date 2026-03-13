package com.lia.liaprove.infrastructure.dtos.ai;

public record SubmissionPreparationInput(
        SubmissionQuestionDraftInput questionDraft,
        AcceptedSuggestionsInput acceptedSuggestions
) {}
