package com.lia.liaprove.core.domain.question;

/**
 * Represents an open question with a guideline and visibility setting.
 */
public class OpenQuestion extends Question {
    private String guideline;
    private OpenQuestionVisibility visibility;

    private static final OpenQuestionVisibility DEFAULT_VISIBILITY = OpenQuestionVisibility.PRIVATE;

    public OpenQuestion() {
        this.visibility = DEFAULT_VISIBILITY;
    }

    public OpenQuestion(String guideline, OpenQuestionVisibility visibility) {
        this.guideline = guideline;
        this.visibility = defaultVisibility(visibility);
    }

    public String getGuideline() {
        return guideline;
    }

    public void setGuideline(String guideline) {
        this.guideline = guideline;
    }

    public OpenQuestionVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(OpenQuestionVisibility visibility) {
        this.visibility = defaultVisibility(visibility);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.OPEN;
    }

    private static OpenQuestionVisibility defaultVisibility(OpenQuestionVisibility visibility) {
        return visibility == null ? DEFAULT_VISIBILITY : visibility;
    }
}
