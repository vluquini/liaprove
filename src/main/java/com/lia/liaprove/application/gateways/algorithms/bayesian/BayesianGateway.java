package com.lia.liaprove.application.gateways.algorithms.bayesian;

import com.lia.liaprove.core.algorithms.bayesian.QuestionFeedbackSummary;
import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.UserRecruiter;

import java.util.List;
import java.util.UUID;

/**
 * Porta que a camada infrastructure deve implementar.
 * Preferencialmente as agregações como getFeedbackSummaryForQuestion devem ser calculadas
 * no banco (consulta agregada) para performance.
 *
 * Campos / dados que um provider deve expor:
 *  - List<UserRecruiter> getAllRecruiters()                          -> recruiter.voteWeight, recruiterUsageCount, recruiterRating
 *  - List<Question> getAllQuestions()                                -> question.recruiterUsageCount, relevanceByLLM, upVote/downVote, knowledgeAreas
 *  - List<FeedbackQuestion> getFeedbacksForQuestion(UUID questionId) -> votes per user, difficulty, relevanceLevel,...
 *
 * Observação: as entidades retornadas são do domínio core (Question, FeedbackQuestion, UserRecruiter).
 */
public interface BayesianGateway {

    /**
     * Retorna resumo (up/down/distro) dos feedbacks para a questão solicitada.
     * Implementação ideal: consulta agregada no BD.
     */
    QuestionFeedbackSummary getFeedbackSummaryForQuestion(UUID questionId);

    /**
     * Retorna todos os recruiters (ou um subconjunto relevante).
     * A implementação pode paginar ou fornecer apenas os recruiters ativos.
     */
    List<UserRecruiter> getAllRecruiters();

    /**
     * Retorna todas as questões candidatas para sugerir (ou um subconjunto).
     * A implementação pode filtrar por status (por exemplo, apenas questões aprovadas/para votação).
     */
    List<Question> getAllQuestions();

    /**
     * (Opcional) Retornar feedbacks brutos quando for necessário (por analítica).
     * Não é obrigatório para o algoritmo principal.
     */
    default List<FeedbackQuestion> getFeedbacksForQuestion(UUID questionId) {
        return List.of();
    }
}
