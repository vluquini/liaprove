package com.lia.liaprove.core.domain.question;

import java.util.*;

/**
 * Representa uma questão de múltipla escolha, estendendo a classe Question e incluindo uma lista de alternativas.
 */
public class MultipleChoiceQuestion extends Question {
    private List<Alternative> alternatives;

    public MultipleChoiceQuestion(){}

    /**
     * Construtor usado pela factory — assume que a lista já foi validada.
     * Faz somente checagens mínimas (não-null) e cópia defensiva.
     */
    public MultipleChoiceQuestion(List<Alternative> alternatives) {
        super();
        if (alternatives == null) {
            throw new IllegalArgumentException("Alternatives must not be null.");
        }
        this.alternatives = List.copyOf(alternatives);
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

}
