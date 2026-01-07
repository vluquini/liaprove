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

    /**
     * Atualiza a lista de alternativas de forma controlada.
     * Garante que a nova lista seja imutável.
     * @param newAlternatives A nova lista de alternativas.
     */
    public void updateAlternatives(List<Alternative> newAlternatives) {
        if (newAlternatives == null) {
            throw new IllegalArgumentException("Alternatives must not be null.");
        }
        this.alternatives = List.copyOf(newAlternatives);
    }

    public void setAlternatives(List<Alternative> alternatives) {
        /*
         * Este setter é intencionalmente fornecido para uso pelo MapStruct durante o mapeamento
         * de entidades JPA para objetos de domínio. MapStruct necessita de um setter público
         * para preencher coleções. A implementação garante uma cópia defensiva para manter
         * a imutabilidade da lista interna, alinhando-se ao design da classe.
         */
        if (alternatives == null) {
            this.alternatives = null;
            return;
        }
        this.alternatives = List.copyOf(alternatives);
    }

}
