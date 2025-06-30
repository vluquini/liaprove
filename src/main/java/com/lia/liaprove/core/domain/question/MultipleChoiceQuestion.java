package com.lia.liaprove.core.domain.question;

import java.util.Set;

public class MultipleChoiceQuestion extends Question {
    // Lista de opções de resposta
    private Set<String> choices;
    private String correctAnswer;

    public MultipleChoiceQuestion(Set<String> choices, String correctAnswer) {
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public Set<String> getChoices() {
        return choices;
    }

    public void setChoices(Set<String> choices) {
        this.choices = choices;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
