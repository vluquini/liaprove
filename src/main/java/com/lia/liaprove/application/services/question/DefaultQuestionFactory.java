package com.lia.liaprove.application.services.question;

import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.usecases.question.QuestionFactory;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementação da Factory para a criação de diferentes tipos de questões.
 * Centraliza a lógica de instanciação e configuração inicial das entidades de Question.
 */
public class DefaultQuestionFactory implements QuestionFactory {

    @Override
    public Question createMultipleChoice(QuestionCreateDto dto) {
        validateFields(dto);

        // Map DTO alternatives -> domain Alternative (JPA will generate id)
        List<Alternative> domainAlternatives = dto.alternatives() == null ? List.of()
                : dto.alternatives().stream()
                .map(req -> new Alternative(null, req.text(), req.correct()))
                .toList();

        // Multiple choice specific validation
        validateMultipleChoiceAlternatives(domainAlternatives);

        // Create already consistent domain object
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(domainAlternatives);

        // Initialize common fields
        initCommonFields(question, dto);

        return question;
    }

    @Override
    public Question createProject(QuestionCreateDto dto) {
        validateFields(dto);

        ProjectQuestion question = new ProjectQuestion();

        initCommonFields(question, dto);

        // specific field of project questions
        question.assignProjectSubmission(null);

        return question;
    }

    /**
     * <p>Valida as alternativas de uma questão de múltipla escolha.</p>
     *
     * <ul>
     *   Regras:
     *   <li>A lista não pode ser nula</li>
     *   <li>Deve conter entre 3 e 5 alternativas</li>
     *   <li>Nenhuma alternativa pode ser nula</li>
     *   <li>O texto de cada alternativa não pode ser nulo ou em branco</li>
     *   <li>Os textos das alternativas devem ser únicos, ignorando maiúsculas,
     *       espaços extras e acentuação</li>
     *   <li>Deve existir exatamente uma alternativa correta</li>
     * </ul>
     *
     * @param alternatives lista de alternativas já mapeadas para o domínio
     * @throws InvalidUserDataException caso alguma regra de validação seja violada
     */
    private void validateMultipleChoiceAlternatives(List<Alternative> alternatives) {
        if (alternatives == null) {
            throw new InvalidUserDataException("Alternatives must not be null.");
        }

        if (alternatives.size() < 3 || alternatives.size() > 5) {
            throw new InvalidUserDataException("Multiple choice questions must have between 3 and 5 alternatives.");
        }

        Set<String> seen = new HashSet<>();
        int correctCount = 0;

        for (int i = 0; i < alternatives.size(); i++) {
            Alternative alt = alternatives.get(i);

            if (alt == null) {
                throw new InvalidUserDataException("Alternative at index " + i + " must not be null.");
            }

            String text = alt.text();

            if (text == null || text.isBlank()) {
                throw new InvalidUserDataException("Alternative text must not be blank.");
            }

            if (alt.correct()) correctCount++;

            String normalized = normalizeText(text);

            if (!seen.add(normalized)) {
                throw new InvalidUserDataException("Alternatives must have unique texts (ignoring case, spaces and accents).");
            }
        }

        if (correctCount != 1) {
            throw new InvalidUserDataException("There must be exactly one correct alternative. Found " + correctCount);
        }
    }

    /**
     * Normaliza um texto para fins de comparação semântica.
     * <ul>
     *   O processo de normalização:
     *   <li>Decompõe caracteres acentuados</li>
     *   <li>Remove acentuação (diacríticos)</li>
     *   <li>Normaliza espaços em branco</li>
     *   <li>Converte o texto para minúsculas.</li>
     * </ul>
     *
     * Este método é utilizado para garantir a unicidade de textos
     * de alternativas independentemente de variações de formatação.
     *
     * @param text o texto original a ser normalizado
     * @return o texto normalizado para comparação
     */
    private String normalizeText(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");       // remove diacríticos
        normalized = normalized.replaceAll("\\s+", " ").trim(); // normaliza espaços
        return normalized.toLowerCase(Locale.ROOT);
    }

    /**
     * Valida os campos básicos comuns à criação de qualquer tipo de questão.
     *
     * As validações realizadas garantem que os dados mínimos necessários
     * para a criação de uma questão válida estejam presentes.
     *
     * @param dto dados fornecidos para criação da questão
     * @throws InvalidUserDataException caso algum campo obrigatório esteja ausente ou inválido
     * @throws NullPointerException caso o DTO seja nulo
     */
    private void validateFields(QuestionCreateDto dto) {
        Objects.requireNonNull(dto, "Question creation data cannot be null");

        if (dto.title() == null || dto.title().isBlank()) {
            throw new InvalidUserDataException("Title must not be empty");
        }
        if (dto.description() == null || dto.description().isBlank()) {
            throw new InvalidUserDataException("Description must not be empty");
        }
        if (dto.knowledgeAreas() == null || dto.knowledgeAreas().isEmpty()) {
            throw new InvalidUserDataException("Knowledge areas must not be empty");
        }
        if (dto.difficultyByCommunity() == null) {
            throw new InvalidUserDataException("Difficulty level mus be provided.");
        }
        if (dto.relevanceByCommunity() == null) {
            throw new InvalidUserDataException("Relevance level mus be provided.");
        }
    }

    /**
     * Inicializa os campos comuns de uma questão recém-criada.
     *
     * <p>Este método aplica valores padrão e dados derivados do contexto
     *    de criação, como data de submissão, status inicial e contadores.
     * </p>
     *
     * @param question instância da questão a ser inicializada
     * @param dto dados de entrada utilizados para popular os campos da questão
     */
    private void initCommonFields(Question question, QuestionCreateDto dto) {
        question.setAuthorId(dto.authorId());
        question.setTitle(dto.title());
        question.setDescription(dto.description());
        question.setKnowledgeAreas(dto.knowledgeAreas());
        question.setDifficultyByCommunity(dto.difficultyByCommunity());
        question.setRelevanceByCommunity(dto.relevanceByCommunity());
        question.setSubmissionDate(LocalDateTime.now());
        question.setStatus(QuestionStatus.VOTING);
        question.setRelevanceByLLM(RelevanceLevel.FIVE); // temporário
        question.setRecruiterUsageCount(0);
    }

}




