package com.lia.liaprove.core.domain.question;

import java.util.List;

public class MultipleChoiceQuestion extends Question {
    // Lista de opções de resposta
    private List<String> choices;
    private String correctAnswer;

    public MultipleChoiceQuestion(List<String> choices, String correctAnswer) {
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
