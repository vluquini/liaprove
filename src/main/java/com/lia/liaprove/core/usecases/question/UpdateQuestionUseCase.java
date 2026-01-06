package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.exceptions.AuthorizationException;
import java.util.*;

/**
 * <p>Interface para o caso de uso de atualização de uma questão após a fase de votação.</p>
 * <p>
 *    Dado a forma como funciona o fluxo de submissão de uma questão, não é necessário
 *    um fluxo para edição de uma questão, a menos que seja para casos administrativos.
 * </p>
 */
public interface UpdateQuestionUseCase {

    /**
     * Executa a atualização de uma questão.
     * <p>Esta operação é restrita a usuários com perfil de {@code ADMIN}.</p>
     *
     * @param actorId O ID do usuário (ator) que está realizando a operação.
     * @param questionId O ID da questão a ser atualizada.
     * @param command Objeto de comando contendo os dados para atualização da questão.
     * @return A questão atualizada, se encontrada, ou um Optional vazio caso contrário.
     * @throws UserNotFoundException se o ator não for encontrado.
     * @throws AuthorizationException se o ator não tiver permissão.
     */
    Optional<Question> execute(UUID actorId, UUID questionId, UpdateQuestionCommand command);

    /**
     * Objeto de comando (DTO) para a atualização de uma questão.
     * Contém os campos que podem ser atualizados. Campos nulos indicam que não devem ser alterados.
     */
    record UpdateQuestionCommand(
            Optional<String> title,
            Optional<String> description,
            Optional<Set<KnowledgeArea>> knowledgeAreas,
            Optional<List<Alternative>> alternatives // Apenas para MultipleChoiceQuestion - deve ser imutável.
    ) {}
}
