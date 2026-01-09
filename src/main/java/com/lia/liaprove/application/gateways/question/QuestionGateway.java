package com.lia.liaprove.application.gateways.question;

import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.QuestionStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Gateway para operações de persistência e recuperação de questões.
 * Define o contrato que a camada de infraestrutura deve implementar para interagir com o armazenamento de dados de questões.
 */
public interface QuestionGateway {

    /**
     * Verifica se uma questão com a descrição fornecida já existe.
     *
     * @param description A descrição da questão a ser verificada.
     * @return true se uma questão com a descrição já existe, false caso contrário.
     */
    boolean existsByDescription(String description);

    /**
     * Salva uma nova questão ou atualiza uma existente.
     *
     * @param question A questão a ser salva ou atualizada.
     * @return A questão salva/atualizada.
     */
    Question save(Question question);

    /**
     * Encontra uma questão pelo seu ID.
     *
     * @param id O ID da questão.
     * @return Um Optional contendo a questão, se encontrada.
     */
    Optional<Question> findById(UUID id);

    /**
     * Encontra questões com base em critérios de filtro.
     *
     * @param knowledgeAreas Conjunto de áreas de conhecimento para filtrar.
     * @param difficultyLevel Nível de dificuldade para filtrar.
     * @param status Status da questão para filtrar.
     * @param authorId ID do autor para filtrar.
     * @param page Número da página para paginação.
     * @param size Tamanho da página para paginação.
     * @return Uma lista de questões que correspondem aos critérios.
     */
    List<Question> findAll(Set<KnowledgeArea> knowledgeAreas, DifficultyLevel difficultyLevel,
                           QuestionStatus status, UUID authorId, int page, int size);

    /**
     * Atualiza uma questão existente.
     *
     * @param question A questão a ser atualizada.
     * @return A questão atualizada.
     */
    Question update(Question question);

    /**
     * Encontra questões com um status específico e cuja data de término da votação é anterior à data e hora especificadas.
     *
     * @param status O status das questões a serem encontradas (ex: VOTING).
     * @param dateTime A data e hora de corte. Questões com votingEndDate anterior a este valor serão retornadas.
     * @return Uma lista de questões que correspondem aos critérios.
     */
    List<Question> findByStatusAndVotingEndDateBefore(QuestionStatus status, java.time.LocalDateTime dateTime);
}
