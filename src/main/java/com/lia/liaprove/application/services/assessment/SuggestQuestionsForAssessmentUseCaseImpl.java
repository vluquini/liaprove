package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.application.services.assessment.dto.SuggestionCriteriaDto;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.algorithms.bayesian.BayesianNetworkUseCase;
import com.lia.liaprove.core.usecases.assessments.SuggestQuestionsForAssessmentUseCase;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementação do caso de uso para sugerir questões para avaliações personalizadas.
 * <p>
 * <b>Nota sobre Paginação e Performance:</b><br>
 * Atualmente, a paginação é realizada em memória (OFFSET via Stream API) após recuperar um pool de sugestões da Rede Bayesiana.
 * Para o MVP/TCC com volume limitado de dados, esta abordagem é suficiente e simplifica a implementação.
 * <p>
 * Futuramente, para escalar, recomenda-se migrar para:
 * <ul>
 *   <li>Paginação via banco de dados (Keyset Pagination) para maior performance com grandes volumes.</li>
 *   <li>Cache de sessão (ex: Redis) para armazenar os IDs já exibidos/excluídos e evitar reprocessamento bayesiano a cada página.</li>
 * </ul>
 */
public class SuggestQuestionsForAssessmentUseCaseImpl implements SuggestQuestionsForAssessmentUseCase {

    private final BayesianNetworkUseCase bayesianNetworkUseCase;
    private final UserGateway userGateway;
    private static final int MAX_SUGGESTIONS_LIMIT = 200; // Aumentado para permitir paginação em memória

    public SuggestQuestionsForAssessmentUseCaseImpl(BayesianNetworkUseCase bayesianNetworkUseCase, UserGateway userGateway) {
        this.bayesianNetworkUseCase = bayesianNetworkUseCase;
        this.userGateway = userGateway;
    }

    @Override
    public List<ScoredQuestion> execute(UUID recruiterId, SuggestionCriteriaDto criteria) {
        // 1. Validar se o usuário é um Recrutador ou Admin
        User user = userGateway.findById(recruiterId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        if (user.getRole() != UserRole.RECRUITER && user.getRole() != UserRole.ADMIN) {
            throw new AuthorizationException("Apenas recrutadores ou administradores podem obter sugestões de questões.");
        }

        // 2. Obter sugestões da rede bayesiana (pool maior)
        List<ScoredQuestion> allSuggestions = bayesianNetworkUseCase.suggestQuestionsForRecruiter(recruiterId, MAX_SUGGESTIONS_LIMIT);

        // 3. Aplicar filtros
        Stream<ScoredQuestion> filteredStream = allSuggestions.stream();

        if (criteria.getQuestionTypes().isPresent()) {
            Set<QuestionType> questionTypes = criteria.getQuestionTypes().get();
            filteredStream = filteredStream.filter(sq -> questionTypes.contains(sq.getQuestion().getQuestionType()));
        }

        // Filtrar por Área de Conhecimento
        if (criteria.getKnowledgeAreas().isPresent()) {
            Set<KnowledgeArea> knowledgeAreas = criteria.getKnowledgeAreas().get();
            filteredStream = filteredStream.filter(sq -> 
                sq.getQuestion().getKnowledgeAreas().stream().anyMatch(knowledgeAreas::contains)
            );
        }

        // Filtrar por Nível de Dificuldade
        if (criteria.getDifficultyLevels().isPresent()) {
            Set<DifficultyLevel> difficultyLevels = criteria.getDifficultyLevels().get();
            filteredStream = filteredStream.filter(sq -> difficultyLevels.contains(sq.getQuestion().getDifficultyByCommunity()));
        }

        // Filtrar apenas questões FINALIZADAS (aprovadas e prontas para uso)
        filteredStream = filteredStream.filter(sq -> sq.getQuestion().getStatus() == QuestionStatus.FINISHED);

        // Open questions ficam visíveis apenas para o próprio autor ou quando marcadas como SHARED.
        filteredStream = filteredStream.filter(sq -> isVisibleForRecruiter(sq.getQuestion(), recruiterId));

        // Filtrar IDs excluídos
        if (criteria.getExcludeIds() != null && !criteria.getExcludeIds().isEmpty()) {
            Set<UUID> excludeSet = new HashSet<>(criteria.getExcludeIds());
            filteredStream = filteredStream.filter(sq -> !excludeSet.contains(sq.getQuestion().getId()));
        }

        // 4. Ordenação e Paginação
        // Ordena por Score DESC, e usa ID ASC para desempate (estabilidade)
        Comparator<ScoredQuestion> comparator = Comparator.comparingDouble(ScoredQuestion::getScore).reversed()
                .thenComparing(sq -> sq.getQuestion().getId());

        int skip = (criteria.getPage() - 1) * criteria.getPageSize();

        return filteredStream
                .sorted(comparator)
                .skip(skip)
                .limit(criteria.getPageSize())
                .collect(Collectors.toList());
    }

    private boolean isVisibleForRecruiter(Question question, UUID recruiterId) {
        if (!(question instanceof OpenQuestion openQuestion)) {
            return true;
        }

        if (Objects.equals(question.getAuthorId(), recruiterId)) {
            return true;
        }

        return openQuestion.getVisibility() == OpenQuestionVisibility.SHARED;
    }
}
