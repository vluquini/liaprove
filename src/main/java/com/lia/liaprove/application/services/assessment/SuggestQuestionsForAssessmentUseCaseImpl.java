package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.application.services.assessment.dto.SuggestionCriteriaDto;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.algorithms.bayesian.BayesianNetworkUseCase;
import com.lia.liaprove.core.usecases.assessments.SuggestQuestionsForAssessmentUseCase;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementação do caso de uso para sugerir questões para avaliações personalizadas.
 */
public class SuggestQuestionsForAssessmentUseCaseImpl implements SuggestQuestionsForAssessmentUseCase {

    private final BayesianNetworkUseCase bayesianNetworkUseCase;
    private final UserGateway userGateway;
    private static final int MAX_SUGGESTIONS_LIMIT = 50; // Para evitar sobrecarga, buscamos um pool maior para depois filtrar.

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

        // 2. Obter sugestões da rede bayesiana
        // Buscamos um número maior de sugestões para ter margem para o filtro.
        List<ScoredQuestion> allSuggestions = bayesianNetworkUseCase.suggestQuestionsForRecruiter(recruiterId, MAX_SUGGESTIONS_LIMIT);

        // 3. Aplicar filtros
        Stream<ScoredQuestion> filteredStream = allSuggestions.stream();

        // Filtrar por Área de Conhecimento, se especificado
        if (criteria.getKnowledgeAreas().isPresent()) {
            Set<KnowledgeArea> knowledgeAreas = criteria.getKnowledgeAreas().get();
            filteredStream = filteredStream.filter(sq -> knowledgeAreas.contains(sq.getQuestion().getKnowledgeAreas()));
        }

        // Filtrar por Nível de Dificuldade, se especificado
        if (criteria.getDifficultyLevels().isPresent()) {
            Set<DifficultyLevel> difficultyLevels = criteria.getDifficultyLevels().get();
            filteredStream = filteredStream.filter(sq -> difficultyLevels.contains(sq.getQuestion().getDifficultyByCommunity()));
        }

        // 4. Limitar o resultado final e coletar
        return filteredStream
                .limit(criteria.getLimit())
                .collect(Collectors.toList());
    }
}
