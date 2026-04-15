package com.lia.liaprove.core.domain.question;

/**
 * Represents an open question with a guideline and visibility setting.
 */
public class OpenQuestion extends Question {
    private String guideline;
    private OpenQuestionVisibility visibility;

    public OpenQuestion() {
    }

    public OpenQuestion(String guideline, OpenQuestionVisibility visibility) {
        this.guideline = guideline;
        this.visibility = visibility;
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
        this.visibility = visibility;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.OPEN;
    }
}
